package iti.kukumo.plugin.api;

import static iti.kukumo.plugin.api.KukumoProperties.SOURCE_TYPES;

import java.util.*;
import java.util.stream.Stream;

import org.jexten.ExtensionManager;

import imconfig.Config;
import iti.kukumo.plugin.api.contributions.*;

@SuppressWarnings("unchecked")
public class Contributions {

    private final Log log = Log.of();
    private final ExtensionManager extensionManager;
    private final Config config;
    private List<? extends ContentType<?>> contentTypes;
    private List<String> contentTypeNames;
    private List<ContentType<?>> sourceContentTypes;
    private List<String> sourceTypeNames;
    private List<? extends SourceDiscoverer<?>> sourceDiscoverers;
    private List<PlanAssembler> planAssemblers;
    private List<PlanMutationRules> planMutationRules;
    private List<PlanExecutionProcessor> executionProcessors;
    private DataTypes dataTypes;
    private Localizer localizer;
    private Config contributedConfig;



    public Contributions(ExtensionManager extensionManager, Config config) {
        this.config = config;
        this.extensionManager = extensionManager.withInjectionProvider(this::inject);
    }



    private Stream<Object> inject (Class<?> type, String name) {
        if (type == Config.class) {
            return Stream.of(contributedConfiguration());
        } else if (type == DataTypes.class) {
            return Stream.of(dataTypes());
        } else if (type == Localizer.class) {
            return Stream.of(localizer());
        } else {
            return Stream.empty();
        }
    }



    public List<ContentType<?>> sourceContentTypes() {
        if (sourceContentTypes == null) {
            this.sourceContentTypes = (List<ContentType<?>>) sourceTypeNames().stream()
                .map(this::contentType)
                .flatMap(Optional::stream).toList();
        }
        return this.sourceContentTypes;
    }


    public List<String> sourceTypeNames() {
        if (sourceTypeNames == null) {
            this.sourceTypeNames = config.getList(SOURCE_TYPES);
        }
        return sourceTypeNames;
    }


    public List<ContentType<?>> contentTypes() {
        if (this.contentTypes == null) {
            this.contentTypes = extensionManager.getExtensions(ContentType.class)
                .map(it -> (ContentType<?>) it)
                .toList();
        }
        return (List<ContentType<?>>) contentTypes;
    }



    public List<String> contentTypeNames() {
        if (contentTypeNames == null) {
            this.contentTypeNames = contentTypes().stream().map(ContentType::name).toList();
        }
        return contentTypeNames;
    }


    public List<SourceDiscoverer<?>> sourceDiscoverers() {
        if (sourceDiscoverers == null) {
            this.sourceDiscoverers = extensionManager.getExtensions(SourceDiscoverer.class)
                .map(it -> (SourceDiscoverer<?>) it)
                .filter(discoverer -> contentTypes.stream().anyMatch(discoverer::accepts))
                .toList();
        }
        return (List<SourceDiscoverer<?>>) sourceDiscoverers;
    }


    public List<PlanExecutionProcessor> executionProcessors() {
        if (executionProcessors == null) {
            this.executionProcessors = extensionManager.getExtensions(PlanExecutionProcessor.class)
                .toList();
        }
        return executionProcessors;
    }


    public Config contributedConfiguration() {
        if (contributedConfig == null) {
            this.contributedConfig = this.config.append(
                extensionManager.getExtensions(ConfigContribution.class)
                    .map(ConfigContribution::config)
                    .reduce(Config.factory().empty(), Config::append)
            );
        }
        return contributedConfig;
    }


    public Stream<SourceDiscoverer<?>> sourceDiscoverers(ContentType<?> contentType) {
        return sourceDiscoverers().stream().filter(it -> it.accepts(contentType));
    }


    public List<PlanAssembler> planAssemblers() {
        if (planAssemblers == null) {
            this.planAssemblers = extensionManager.getExtensions(PlanAssembler.class)
                .filter(it -> sourceContentTypes().stream().anyMatch(it::accepts))
                .toList();
        }
        return planAssemblers;
    }


    public Optional<PlanAssembler> planAssembler(ContentType<?> contentType) {
        return planAssemblers().stream().filter(it->it.accepts(contentType)).findFirst();
    }


    public List<PlanMutationRules> planMutationRules() {
        if (planMutationRules == null) {
            this.planMutationRules = extensionManager.getExtensions(PlanMutationRules.class)
                .toList();
        }
        return planMutationRules;
    }


    public DataTypes dataTypes() {
        if (dataTypes == null) {
            this.dataTypes = new DataTypes(extensionManager);
        }
        return dataTypes;
    }


    public Localizer localizer() {
        if (localizer == null) {
            this.localizer = new Localizer(extensionManager);
        }
        return localizer;
    }


    public Optional<? extends ContentType<?>> contentType(String name) {
        var result = contentTypes().stream()
            .filter(it -> it.name().equals(name) || it.aliases().contains(name))
            .findFirst();
        if (result.isEmpty()) {
            log.warn("""
                There is no content type related to name {content-type}.
                    Accepted content type names are: {}""",
                name,
                String.join(", ", contentTypeNames())
            );
        }
        return result;
    }





}
