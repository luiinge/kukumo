package iti.kukumo.api;

import iti.commons.configurer.Configuration;
import iti.commons.jext.Extension;
import iti.commons.jext.ExtensionManager;
import iti.kukumo.api.event.EventDispatcher;
import iti.kukumo.api.extensions.*;
import iti.kukumo.api.plan.PlanNode;
import iti.kukumo.api.plan.PlanNodeDescriptor;
import iti.kukumo.api.plan.PlanSerializer;
import iti.kukumo.core.plan.DefaultPlanSerializer;
import iti.kukumo.core.runner.PlanRunner;
import iti.kukumo.util.ResourceLoader;
import iti.kukumo.util.TagFilter;
import iti.kukumo.util.ThrowableFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Kukumo {

    private static final Logger LOGGER = LoggerFactory.getLogger("kukumo.logs");

    private static final ExtensionManager extensionManager =
            new ExtensionManager(Thread.currentThread().getContextClassLoader());

    private static ResourceLoader resourceLoader;
    private static EventDispatcher eventDispatcher;
    private static final PlanSerializer planSerializer = new DefaultPlanSerializer();





    /**
     * Attempt to create a iti.kukumo.test.gherkin.plan using the resource type and the feature path defined in then received configuration.
     * @param configuration
     * @return A new iti.kukumo.test.gherkin.plan ready to be executed
     * @throws KukumoException if the iti.kukumo.test.gherkin.plan couldn't be created
     */
    public static PlanNode createPlanFromConfiguration(Configuration configuration) throws KukumoException {

        Optional<String> resourceTypeName = configuration.getString(KukumoConfiguration.RESOURCE_TYPE);
        ResourceType<?> resourceType;
        if (resourceTypeName.isPresent()) {
            resourceType = nonOptional(getResourceTypeByName(resourceTypeName.get()),
                "Resource type '{}' is not provided by any contributor",resourceTypeName.get());
        } else {
            resourceType = nonOptional(extensionManager.getExtension(ResourceType.class),
                "No resource types are provided by any contributor");
        }

        List<String> discoveryPaths = configuration.getStringList(KukumoConfiguration.RESOURCE_PATH);


        List<Resource<?>> resources = getResourceLoader().discoverResources(discoveryPaths, resourceType);
        Planner planner = nonOptional(getPlannerFor(resourceType),
                "No planner suitable for resource type {} has been found",resourceType);

        return configure(planner,configuration).createPlan(resources);
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


    public static <T> void publishEvent(String eventType, T data) {
        getEventDispatcher().publishEvent(eventType, data);
    }


    public static PlanNode executePlan(PlanNode plan, Configuration configuration) throws IOException {
        PlanNode result = new PlanRunner(plan, configuration).run();
        if (configuration.getBoolean(KukumoConfiguration.REPORT_GENERATION).orElse(true)) {
            Kukumo.report(configuration);
        }
        return result;
    }




    public static void report(Configuration configuration) throws IOException {
        List<Reporter> reporters = extensionManager.getExtensions(Reporter.class);
        if (reporters.isEmpty()) {
            return;
        }
        Path sourceFolder = Paths.get(nonOptional(
            configuration.getString(KukumoConfiguration.REPORT_SOURCE),
            "Report source is not defined. Please configure property {}",
            KukumoConfiguration.REPORT_SOURCE
        ));
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
                configure(reporter,configuration).report(rootNode);
            } catch (Exception e) {
                LOGGER.error("Error running reporter {} : {}", reporter.info(), e.getMessage(), e);
            }
        }


    }



}
