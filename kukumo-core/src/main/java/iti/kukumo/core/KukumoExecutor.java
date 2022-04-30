package iti.kukumo.core;

import static iti.kukumo.plugin.api.lang.Functions.or;

import java.time.Clock;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.function.*;
import java.util.stream.Collectors;

import org.jexten.*;
import org.jexten.plugin.*;

import imconfig.Config;
import iti.kukumo.core.backend.*;
import iti.kukumo.core.concurrency.ExecutorProvider;
import iti.kukumo.core.exceptions.KukumoException;
import iti.kukumo.core.runner.*;
import iti.kukumo.plugin.api.*;
import iti.kukumo.plugin.api.contributions.SourceDiscoverer;
import iti.kukumo.plugin.api.plan.*;
import iti.kukumo.plugin.api.sources.Source;
import lombok.Setter;

public class KukumoExecutor {



    public static Builder builder() {
        return new Builder();
    }

    @Setter
    public static class Builder {

        private Clock clock;
        private Config configuration;
        private PluginWarehouse pluginWarehouse;

        public KukumoExecutor build() {
            return new KukumoExecutor(configuration, pluginWarehouse, clock);
        }
    }



    private record PluginId (String group, String name) {
        public PluginId(String coordinates) {
            this(coordinates.split(":")[0],coordinates.split(":")[1]);
        }
    }


    private final Log log = Log.of();
    private final Config config;
    private final ExtensionManager extensionManager;
    private final Contributions contributions;
    private final Clock clock;


    KukumoExecutor(Config conf, PluginWarehouse pluginWarehouse, Clock clock) {
        var requestConf =  or(conf, Config.factory()::empty);

        var requestPlugins = requestConf.getStream(KukumoProperties.PLUGINS)
            .map(PluginId::new)
            .toList();

        Predicate<Coordinates> pluginFilter = requestPlugins.isEmpty() ?
            it -> true :
            it -> requestPlugins.stream().anyMatch(
                id -> id.group.equals(it.group()) && id.name.equals(it.name())
            );

        this.clock = or(clock, Clock::systemDefaultZone);
        this.extensionManager = ExtensionManager.create(ModuleLayerProvider.compose(
            ModuleLayerProvider.boot(),
            PluginModuleLayerProvider.builder()
                .pluginFilter(pluginFilter)
                .addPluginWarehouse(pluginWarehouse)
                .build()
        ));
        this.contributions = new Contributions(extensionManager, requestConf);
        this.config = DefaultConfiguration.CONFIGURATION
            .append(contributions.contributedConfiguration())
            .append(requestConf);
    }



    public PlanExecution executeTestPlan() {
        return run(createMetadata());
    }


    private PlanExecution.ExecutionMetadata createMetadata() {
        return new PlanExecution.ExecutionMetadata(
            UUID.randomUUID().toString(),
            "owner", // TODO
            clock.instant()
        );
    }


    private PlanExecution run(PlanExecution.ExecutionMetadata metadata) {

        log.debug("Kukumo run starts");

        if (contributions.contentTypes().isEmpty()) {
            throw new KukumoException("There is no content type contribution installed!");
        }

        if (contributions.sourceContentTypes().isEmpty()) {
            throw new KukumoException("""
                There is no content type contribution for any of the specified source types ({}).
                    Accepted content type names are: {}""",
                String.join(", ", contributions.sourceTypeNames()),
                String.join(", ", contributions.contentTypeNames())
            );
        }

        if (contributions.sourceDiscoverers().isEmpty()) {
            throw new KukumoException(
                "There is no source discoverer for any of the specified source types ({})",
                String.join(", ", contributions.sourceTypeNames())
            );
        }

        log.debug("Discovering sources...");
        var sources = discoverSources();
        if (sources.isEmpty()) {
            throw new KukumoException("No sources found given the current configuration.");
        }

        log.debug("Assembling sources...");
        var plan = assembleSources(sources);
        if (plan == null) {
            throw new KukumoException("The test plan is empty");
        }

        log.debug("Applying plan mutation rules...");
        for (var mutationRules : contributions.planMutationRules()) {
            mutationRules.rules().forEach(runSafely(rule -> rule.mutate(plan)));
        }

        log.debug("Preparing execution context...");
        var executablePlan = new ExecutablePlanNode(plan, this.config);
        BackendFactory backendFactory = new DefaultBackendFactory(
            this.config,
            this.contributions,
            this.extensionManager
        );
        var singleThreadExecutor = Executors.newSingleThreadExecutor();
        ExecutorProvider executorProvider = it -> singleThreadExecutor;
        var context = new RunnerContext(metadata,backendFactory,executorProvider,clock);
        var runner = new PlanNodeRunner(context, executablePlan);

        log.debug("Running test plan...");
        var planResult = runner.run();
        var planExecution = new PlanExecution(metadata, planResult);

        log.debug("Executing result processors...");
        contributions.executionProcessors()
            .stream()
            .parallel()
            .forEach(runSafely(it -> it.processExecution(planExecution)));

        log.debug("Kukumo run ends.");
        return planExecution;

    }


    private List<Source<?>> discoverSources() {
        return contributions.sourceContentTypes().stream()
            .flatMap(applySafely(contributions::sourceDiscoverers))
            .flatMap(applySafely(SourceDiscoverer::discoverSources))
            .collect(toList());
    }


    private MutablePlanNode assembleSources(List<Source<?>> sources) {
        List<MutablePlanNode> sourceNodes = new ArrayList<>();
        for (var source : sources) {
            var planAssembler = contributions.planAssembler(source.contentType()).orElse(null);
            if (planAssembler == null) {
                log.warn("There is no plan assembler suitable for source {}", source);
            } else {
                planAssembler.assembleTestPlan(source).ifPresent(sourceNodes::add);
            }
        }
        return MutablePlanNode.compose(sourceNodes);
    }



    private static <T>  java.util.stream.Collector<T, ?, java.util.List<T>> toList() {
        return Collectors.toUnmodifiableList();
    }




    private <T,U> Function<T,U> applySafely(Function<T,U> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Throwable e) { //NOSONAR
                log.error(e, "Unexpected fail");
                return null;
            }
        };
    }

    private <T> Consumer<T> runSafely(Consumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Throwable e) { //NOSONAR
                log.error(e, "Unexpected fail");
            }
        };
    }


}
