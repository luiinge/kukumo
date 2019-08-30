package iti.kukumo.api;

import iti.commons.configurer.Configuration;
import iti.commons.jext.Extension;
import iti.commons.jext.ExtensionManager;
import iti.commons.slf4jjansi.AnsiLogger;
import iti.kukumo.api.event.EventDispatcher;
import iti.kukumo.api.extensions.*;
import iti.kukumo.api.plan.PlanNode;
import iti.kukumo.api.plan.PlanNodeDescriptor;
import iti.kukumo.api.plan.PlanSerializer;
import iti.kukumo.core.plan.DefaultPlanNode;
import iti.kukumo.core.plan.DefaultPlanSerializer;
import iti.kukumo.core.runner.PlanRunner;
import iti.kukumo.util.KukumoLogger;
import iti.kukumo.util.ResourceLoader;
import iti.kukumo.util.TagFilter;
import iti.kukumo.util.ThrowableFunction;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static iti.kukumo.api.KukumoConfiguration.*;



public class Kukumo {

    public static final Logger LOGGER = KukumoLogger.forClass(Kukumo.class);

    private static final ExtensionManager extensionManager =
            new ExtensionManager(Thread.currentThread().getContextClassLoader());

    private static ResourceLoader resourceLoader;
    private static EventDispatcher eventDispatcher;
    private static final PlanSerializer planSerializer = new DefaultPlanSerializer();


    static {
        LOGGER.info(KukumoLogger.logo());
    }


    /**
     * Enable / disable Ansi characters in logs according the given configuration
     * @param configuration
     */
    public static void setAnsiFromConfiguration(Configuration configuration) {
        AnsiLogger.setAnsiEnabled(
           configuration.get(LOGS_ANSI_ENABLED,Boolean.class).orElse(true)
        );
    }



    /**
     * Attempt to create a iti.kukumo.test.gherkin.plan using the resource type and the feature
     * path defined in then received configuration.
     * @param configuration
     * @return A new iti.kukumo.test.gherkin.plan ready to be executed
     * @throws KukumoException if the iti.kukumo.test.gherkin.plan couldn't be created
     */
    public static PlanNode createPlanFromConfiguration(Configuration configuration) {

        LOGGER.info("{message}","Creating the Test Plan...");
        List<String> resourceTypeNames = configuration.getList(RESOURCE_TYPES,String.class);
        if (resourceTypeNames.isEmpty()) {
            throw new KukumoException("No resource types configured");
        }
        List<String> discoveryPaths = new ArrayList<>(configuration.getList(RESOURCE_PATH,String.class));
        if (discoveryPaths.isEmpty()) {
            discoveryPaths.add(".");
        }
        List<PlanNode> plans = new ArrayList<>();
        for (String resourceTypeName : resourceTypeNames) {
            Optional<PlanNode> plan = createPlanForResourceType(resourceTypeName, discoveryPaths, configuration);
            plan.ifPresent(plans::add);
        }
        if (plans.isEmpty()) {
            throw new KukumoException("No test plans created");
        }
        return mergePlans(plans);
    }



    private static PlanNode mergePlans(List<PlanNode> plans) {
        if (plans.isEmpty()) {
            return null;
        }
        if (plans.size() == 1) {
            return plans.get(0);
        }
        PlanNode root = new DefaultPlanNode<>("root");
        plans.forEach(root::addChild);
        return root;
    }



    public static Optional<PlanNode> createPlanForResourceType(String resourceTypeName, List<String> discoveryPaths, Configuration configuration) {
        Optional<ResourceType<?>> resourceType = getResourceTypeByName(resourceTypeName);
        if (!resourceType.isPresent()) {
            LOGGER.warn("{warn} {resourceType} {warn}","Resource type",resourceTypeName,"is not provided by any contributor");
            return Optional.empty();
        }
        LOGGER.debug("Creating plan for resources of type {resourceType} provided by {contributor}...", resourceTypeName, resourceType.get().info());
        List<Resource<?>> resources = getResourceLoader().discoverResources(discoveryPaths, resourceType.get());
        if (resources.isEmpty()) {
            LOGGER.warn("{warn} {resourceType}","No resources of type",resourceTypeName);
            return Optional.empty();
        }
        Optional<Planner> planner = getPlannerFor(resourceType.get());
        if (!planner.isPresent()) {
            LOGGER.warn("{warn} {resourceType} {warn}","No planner suitable for resource type",resourceType," has been found");
            return Optional.empty();
        }
        return Optional.of(configure(planner.get(),configuration).createPlan(resources));
    }







    public static Optional<Planner> getPlannerFor(ResourceType<?> resourceType) {
        Predicate<Planner> filter = planner->planner.acceptResourceType(resourceType);
        return extensionManager.getExtensionThatSatisfy(Planner.class, filter);
    }



    @SuppressWarnings({ "rawtypes" })
    public static List<ResourceType<?>> availableResourceTypes() {
        List<ResourceType> resourceTypes = extensionManager.getExtensions(ResourceType.class);
        return resourceTypes.stream().map(x->(ResourceType<?>)x).collect(Collectors.toList());
    }


    public static Optional<ResourceType<?>> getResourceTypeByName(String name) {
        return availableResourceTypes().stream().filter(
                resourceType -> resourceType.extensionMetadata().name().equals(name)
        ).findAny();
    }


    public static List<DataTypeContributor> getSpecificDataTypeContributors(List<String> modules) {
        Predicate<Extension> condition = extension -> modules.contains(extension.name());
        return extensionManager.getExtensionsThatSatisfyMetadata(DataTypeContributor.class, condition);
    }


    public static List<DataTypeContributor> getAllDataTypeContributors() {
        return extensionManager.getExtensions(DataTypeContributor.class);
    }


    public static List<StepContributor> loadSpecificStepContributors(List<String> modules, Configuration configuration) {
        Predicate<Extension> condition = extension -> modules.contains(extension.name());
        List<StepContributor> stepContributors = extensionManager.getExtensionsThatSatisfyMetadata(StepContributor.class,condition);
        stepContributors.forEach(c->configure(c,configuration));
        return stepContributors;
    }

    public static List<StepContributor> loadAllStepContributors(Configuration configuration) {
        List<StepContributor> stepContributors = extensionManager.getExtensions(StepContributor.class);
        stepContributors.forEach(c->configure(c,configuration));
        return stepContributors;
    }

    public static List<Extension> getAllStepContributorMetadata() {
        return extensionManager.getExtensionMetadata(StepContributor.class);
    }


    public static ResourceLoader getResourceLoader() {
        if (resourceLoader == null) {
            resourceLoader = new ResourceLoader();
        }
        return resourceLoader;
    }



    public static TagFilter getTagFilter(String tagExpression) {
        return new TagFilter(tagExpression);
    }

    public static BackendFactory getBackendFactory() {
        return nonOptional(extensionManager.getExtension(BackendFactory.class),"Cannot get an instance of BackendFactory");
    }

    public static PlanSerializer getPlanSerializer() {
        return planSerializer;
    }

    public static ExtensionManager getExtensionManager() {
        return extensionManager;
    }

    private static <T> T nonOptional(Optional<T> optional, String errorMessage, Object... messageArgs) {
        return optional.orElseThrow(()->new KukumoException(errorMessage,messageArgs));
    }


    private Kukumo() { /* avoid instantiation */ }


    @SuppressWarnings("unchecked")
    public static <T> T configure( T contributor, Configuration configuration) {
        for (Configurator<T> configurator : extensionManager.getExtensions(Configurator.class)) {
            if (configurator.accepts(contributor)) {
                configurator.configure(contributor, configuration);
            }
        }
        return contributor;
    }



    private static EventDispatcher getEventDispatcher() {
        if (eventDispatcher == null) {
            eventDispatcher = new EventDispatcher();
            extensionManager.getExtensions(EventObserver.class).forEach(eventDispatcher::addObserver);
        }
        return eventDispatcher;
    }


    public static void configureEventObservers(Configuration configuration) {
        getEventDispatcher().observers().forEach(observer -> configure(observer,configuration));
    }

    public static void addEventDispatcherObserver(EventObserver observer) {
        getEventDispatcher().addObserver(observer);
    }

    public static void removeEventDispatcherObserver(EventObserver observer) {
        getEventDispatcher().removeObserver(observer);
    }


    public static <T> void publishEvent(String eventType, T data) {
        getEventDispatcher().publishEvent(eventType, data);
    }



    public static PlanNode executePlan(PlanNode plan, Configuration configuration)
    throws IOException {
        PlanNode result = new PlanRunner(plan, configuration).run();
        if (configuration.get(KukumoConfiguration.REPORT_GENERATION,Boolean.class).orElse(true)) {
            Kukumo.report(configuration);
        }
        return result;
    }




    public static void report(Configuration configuration) throws IOException {
        List<Reporter> reporters = extensionManager.getExtensions(Reporter.class);
        if (reporters.isEmpty()) {
            return;
        }
        LOGGER.info("{message}","Generating reports...");
        String reportSource = configuration.get(REPORT_SOURCE, String.class)
             .orElse( configuration.get(OUTPUT_FILE_PATH, String.class).orElse(null) );
        Path sourceFolder = Paths.get(reportSource).toAbsolutePath();
        if (!Files.exists(sourceFolder)) {
            throw new KukumoException(
                "The report source file/folder "+sourceFolder+" does not exist.\n"+
                "Perhaps you may set the property "+REPORT_SOURCE+" to the path defined by the property "+OUTPUT_FILE_PATH+": "+
                configuration.get(OUTPUT_FILE_PATH,String.class).orElse("<undefined>")
            );
        }
        PlanSerializer deserializer = getPlanSerializer();
        PlanNodeDescriptor[] plans;
        try ( Stream<Path> walker = Files.walk(sourceFolder)) {
            plans = walker
            .map(Path::toFile)
            .filter(File::exists)
            .filter(File::isFile)
            .map(ThrowableFunction.unchecked(deserializer::read))
            .toArray(PlanNodeDescriptor[]::new);
        }
        PlanNodeDescriptor rootNode = PlanNodeDescriptor.group(plans);
        for (Reporter reporter : reporters) {
            try {
                LOGGER.debug("Generating report provided by plugin {contributor}...",reporter.info());
                configure(reporter,configuration).report(rootNode);
            } catch (Exception e) {
                LOGGER.error("{error} {contributor} : {error}","Error running reporter", reporter.info(), e.getMessage(), e);
            }
        }


    }



}
