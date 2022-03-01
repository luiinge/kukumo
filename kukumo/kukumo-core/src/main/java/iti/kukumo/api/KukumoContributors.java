/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package iti.kukumo.api;


import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;



import iti.commons.configurer.Configurable;
import iti.commons.configurer.Configuration;
import iti.commons.configurer.ConfigurationBuilder;
import iti.commons.jext.Extension;
import iti.commons.jext.ExtensionManager;
import iti.kukumo.api.event.Event;
import iti.kukumo.api.extensions.*;
import iti.kukumo.util.Pair;


public class KukumoContributors {

    private ExtensionManager extensionManager = new ExtensionManager();

    public void setClassLoaders(ClassLoader... loaders) {
        this.extensionManager = new ExtensionManager(loaders);
    }

    private final List<StepContributor> nonRegisteredStepContributors = new LinkedList<>();

    public Map<Class<?>,List<Contributor>> allContributors() {
        Class<?>[] contributorTypes = {
                ConfigContributor.class,
                DataTypeContributor.class,
                EventObserver.class,
                PlanBuilder.class,
                PlanTransformer.class,
                Reporter.class,
                ResourceType.class,
                StepContributor.class
        } ;
        Map<Class<?>,List<Contributor>> map = Stream.of(contributorTypes)
            .map(type -> new Pair<>(type,extensionManager.getExtensions(type).map(Contributor.class::cast).collect(Collectors.toList())))
            .collect(Collectors.toMap(Pair::key,Pair::value));
        map.get(StepContributor.class).addAll(nonRegisteredStepContributors);
        return map;
    }

    public <T extends Contributor> T getContributor(Class<T> contributorClass) {
        return allContributors().entrySet().stream()
                .flatMap(e -> e.getValue().stream())
                .filter(c -> contributorClass.isAssignableFrom(c.getClass()))
                .map(contributorClass::cast)
                .findFirst()
                .orElseThrow(() -> new KukumoException(String.format("Contributor [%s] not found", contributorClass)));
    }

    public void addNonRegisteredContributors(List<StepContributor> contributor) {
        nonRegisteredStepContributors.addAll(contributor);
    }

    public Stream<EventObserver> eventObservers() {
        return extensionManager.getExtensions(EventObserver.class);
    }


    public Optional<PlanBuilder> createPlanBuilderFor(
        ResourceType<?> resourceType,
        Configuration configuration
    ) {
        Predicate<PlanBuilder> filter = planner -> planner.acceptResourceType(resourceType);
        Optional<PlanBuilder> planBuilder = extensionManager
            .getExtensionThatSatisfy(PlanBuilder.class, filter);
        if (planBuilder.isPresent()) {
            configure(planBuilder.get(), configuration);
        }
        return planBuilder;
    }


    /**
     * @return A list of all available resource types provided by contributors
     */
    public Stream<ResourceType<?>> availableResourceTypes() {
        return extensionManager.getExtensions(ResourceType.class).map(x -> (ResourceType<?>) x);
    }


    public Optional<ResourceType<?>> resourceTypeByName(String name) {
        return availableResourceTypes().filter(
            resourceType -> resourceType.extensionMetadata().name().equals(name)
        ).findAny();
    }


    public Stream<DataTypeContributor> dataTypeContributors(List<String> modules) {
        Predicate<Extension> condition = extension -> modules.contains(extension.name());
        return extensionManager
            .getExtensionsThatSatisfyMetadata(DataTypeContributor.class, condition);
    }


    public Stream<DataTypeContributor> allDataTypeContributors() {
        return extensionManager.getExtensions(DataTypeContributor.class);
    }


    public List<StepContributor> createStepContributors(
        List<String> modules,
        Configuration configuration
    ) {
        Predicate<Extension> condition = extension -> modules.contains(extension.name());
        return extensionManager
            .getExtensionsThatSatisfyMetadata(StepContributor.class, condition)
            .peek(c -> configure(c, configuration))
            .collect(Collectors.toList());
    }


    public List<StepContributor> createAllStepContributors(Configuration configuration) {
        return extensionManager
            .getExtensions(StepContributor.class)
            .peek(c -> configure(c, configuration))
            .collect(Collectors.toList());
    }


    public Stream<Extension> allStepContributorMetadata() {
        return extensionManager.getExtensionMetadata(StepContributor.class);
    }


    @SuppressWarnings("unchecked")
    public <T> Stream<ConfigContributor<T>> configuratorsFor(T contributor) {
        return extensionManager
            .getExtensionsThatSatisfy(ConfigContributor.class, c -> c.accepts(contributor))
            .map(c -> (ConfigContributor<T>) c);
    }



    public <T> T configure(T contributor, Configuration configuration) {
        if (contributor instanceof Configurable) {
            ((Configurable) contributor).configure(configuration);
        }
        configuratorsFor(contributor).forEach(configurator -> {
            configurator.configurer().configure(contributor, configurator.defaultConfiguration().append(configuration));
        });
        return contributor;
    }


    public Stream<PlanTransformer> planTransformers() {
        return extensionManager.getExtensions(PlanTransformer.class);
    }


    public Stream<Reporter> reporters() {
        return extensionManager.getExtensions(Reporter.class);
    }


    public ExtensionManager extensionManager() {
        return extensionManager;
    }


    public Configuration globalDefaultConfiguration() {
        return extensionManager.getExtensions(ConfigContributor.class)
        .map(ConfigContributor::defaultConfiguration)
        .reduce(ConfigurationBuilder.instance().empty(), Configuration::append);
    }



    private <T> Stream<T> concat(Stream<? extends T>... streams) {
        Stream<T> concat = Stream.empty();
        for (Stream<? extends T> stream : streams) {
            concat = Stream.concat(concat,stream);
        }
        return concat;
    }

}
