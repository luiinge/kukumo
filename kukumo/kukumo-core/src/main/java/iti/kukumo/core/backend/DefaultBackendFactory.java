package iti.kukumo.core.backend;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import iti.commons.configurer.Configuration;
import iti.commons.jext.Extension;
import iti.kukumo.api.Backend;
import iti.kukumo.api.BackendFactory;
import iti.kukumo.api.Kukumo;
import iti.kukumo.api.KukumoConfiguration;
import iti.kukumo.api.KukumoDataType;
import iti.kukumo.api.KukumoDataTypeRegistry;
import iti.kukumo.api.KukumoException;
import iti.kukumo.api.annotations.I18nResource;
import iti.kukumo.api.annotations.SetUp;
import iti.kukumo.api.annotations.Step;
import iti.kukumo.api.annotations.TearDown;
import iti.kukumo.api.extensions.DataTypeContributor;
import iti.kukumo.api.extensions.StepContributor;
import iti.kukumo.api.plan.PlanNode;
import iti.kukumo.util.ThrowableRunnable;

@Extension(provider="iti.kukumo", name="defaultBackendFactory", version="1.0-SNAPSHOT")
public class DefaultBackendFactory implements BackendFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBackendFactory.class);
    private static final List<String> DEFAULT_MODULES = Collections.unmodifiableList(Arrays.asList(
            "core-types",
            "assertion-types"
    ));


    private Clock clock = Clock.systemUTC();
    private Configuration configuration;


    @Override
    public BackendFactory setClock(Clock clock) {
        this.clock = clock;
        return this;
    }

    @Override
    public BackendFactory setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        return this;
    }



    @Override
    public Backend createBackend(PlanNode node) {
        if (clock == null) {
            throw new IllegalStateException("Clock must be set first");
        }
        if (configuration == null) {
            throw new IllegalStateException("Configuration must be set first");
        }
        LOGGER.debug("----------------------------------------------------------------------");
        LOGGER.debug("creating backend for {}::'{}'", node.source(), node.displayName());
        Backend backend =  createBackend(configuration.appendFromMap(node.properties()));
        LOGGER.debug("----------------------------------------------------------------------");
        return backend;
    }



    protected Backend createBackend(Configuration configuration) {

        // each backend uses a new instance of each step contributor in order to ease parallelization

        List<StepContributor> stepContributors;
        List<DataTypeContributor> dataTypeContributors;

        List<String> restrictedModules = new ArrayList<>(configuration.getStringList(KukumoConfiguration.MODULES));
        if (restrictedModules.isEmpty()) {
            stepContributors = Kukumo.loadAllStepContributors(configuration);
            dataTypeContributors = Kukumo.getAllDataTypeContributors();
        } else {
            restrictedModules.addAll(DEFAULT_MODULES);
            stepContributors = Kukumo.loadSpecificStepContributors(restrictedModules,configuration);
            dataTypeContributors = Kukumo.getSpecificDataTypeContributors(restrictedModules);
        }

        if (dataTypeContributors.isEmpty()) {
            LOGGER.warn("No data type contributors found!");
        }

        List<String> nonRegisteredContributorClasses = configuration.getStringList(KukumoConfiguration.NON_REGISTERED_STEP_PROVIDERS);
        if (LOGGER.isTraceEnabled()) {
            String nonRegisteredContributorInfo = nonRegisteredContributorClasses.stream().collect(Collectors.joining(", "));
            LOGGER.trace("using configuration (restrictedModules={}, additionalStepProviders={} )...", restrictedModules, nonRegisteredContributorInfo);
            LOGGER.trace("{}",configuration);
        }
        KukumoDataTypeRegistry typeRegistry = loadTypes(dataTypeContributors);
        List<RunnableStep> steps = loadSteps(stepContributors, typeRegistry, nonRegisteredContributorClasses);
        List<ThrowableRunnable> setUpOperations = loadMethods(stepContributors, SetUp.class, SetUp::order);
        List<ThrowableRunnable> tearDownOperations = loadMethods(stepContributors, TearDown.class, TearDown::order);
        return new DefaultBackend(configuration,typeRegistry,steps,setUpOperations,tearDownOperations,clock);
    }



    private <A extends Annotation> List<ThrowableRunnable> loadMethods(
        List<StepContributor> stepContributors, 
        Class<A> annotation, 
        Function<A,Integer> orderProvider
    ) {
        LinkedHashMap<ThrowableRunnable,A> runnables = new LinkedHashMap<>();
        for (StepContributor stepContributor : stepContributors) {
            for (Method method : stepContributor.getClass().getMethods()) {
                if (method.isAnnotationPresent(annotation)) {
                    runnables.put(
                        args -> method.invoke(stepContributor),
                        method.getAnnotation(annotation)                         
                    );
                }
            }
        }
        return runnables.entrySet().stream()
        .sorted( (e1,e2)->orderProvider.apply(e1.getValue()).compareTo(orderProvider.apply(e2.getValue())) )
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
    }



    protected KukumoDataTypeRegistry loadTypes(List<DataTypeContributor> contributors) {

        Map<String,KukumoDataType<?>> types = new HashMap<>();
        for (DataTypeContributor contributor: contributors) {
            for (KukumoDataType<?> type : contributor.contributeTypes()) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("using type {}:{} ({})", contributor.info(), type.getName(), type.getJavaType());
                }
                KukumoDataType<?> replacedType = types.put(type.getName(), type);
                if (replacedType != null && LOGGER.isDebugEnabled()) {
                    LOGGER.warn("Module {} overrides type {}", contributor.info(), replacedType.getName());
                }
            }
        }
        return new KukumoDataTypeRegistry(types);
    }




    protected List<RunnableStep> loadSteps(
            List<StepContributor> stepContributors,
            KukumoDataTypeRegistry typeRegistry,
            List<String> nonRegisteredContributorClasses
    ) {
        ArrayList<RunnableStep> resultSteps = new ArrayList<>();
        List<Object> allStepContributors = new ArrayList<>();
        allStepContributors.addAll(stepContributors);
        allStepContributors.addAll(resolveNonRegisteredContributors(nonRegisteredContributorClasses));
        if (allStepContributors.isEmpty()) {
            LOGGER.error(
                    "No step contributors found. You must either declare step modules with property '{}' or "+
                    "non-registered step provider classes with property '{}'",
                    KukumoConfiguration.MODULES, KukumoConfiguration.NON_REGISTERED_STEP_PROVIDERS
            );
            throw new KukumoException("No step contributors found!");
        }
        for (Object stepContributor : allStepContributors) {
            loadContributorSteps(resultSteps,stepContributor,typeRegistry);
        }
        return resultSteps;
    }



    protected List<Object> resolveNonRegisteredContributors(List<String> nonRegisteredContributorClasses) {
        List<Object> nonRegisteredContributors = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for (String nonRegisteredContributorClass : nonRegisteredContributorClasses) {
            try {
                Object newStepContributor = classLoader.loadClass(nonRegisteredContributorClass).getConstructor().newInstance();
                if (newStepContributor instanceof StepContributor) {
                    Kukumo.configure(newStepContributor,configuration);
                    nonRegisteredContributors.add(newStepContributor);
                } else {
                    LOGGER.warn("Class {} does not implement {}", nonRegisteredContributorClass, StepContributor.class);
                }
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Cannot find non-registered step provider class: {}", nonRegisteredContributorClass);
            } catch (NoSuchMethodException e) {
                LOGGER.warn("Non-registered step provider class {} requieres empty constructor", nonRegisteredContributorClass);
            } catch (ReflectiveOperationException e) {
                LOGGER.warn("Error loading non-registered step provider class {} : {}", nonRegisteredContributorClass, e.getLocalizedMessage());
            }
        }
        return nonRegisteredContributors;
    }


    protected void loadContributorSteps(List<RunnableStep> output, Object stepProvider, KukumoDataTypeRegistry typeRegistry) {
        for (Method method : stepProvider.getClass().getMethods()) {
            if (method.isAnnotationPresent(Step.class)) {
                try {
                    RunnableStep step = createRunnableStep(stepProvider, method, typeRegistry);
                    output.add(step);
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("using step <{}::'{}' {}>",
                                stepProvider.getClass().getSimpleName(), step.getDefinitionKey(), step.getArguments());
                    }
                } catch (Exception e) {
                    throw new KukumoException(e);
                }
            }
        }
    }







    protected RunnableStep createRunnableStep(
            Object runnableObject,
            Method runnableMethod,
            KukumoDataTypeRegistry typeRegistry
    ) {
        final I18nResource stepDefinitionFile = runnableObject.getClass().getAnnotation(I18nResource.class);
        if (stepDefinitionFile == null) {
            throw new KukumoException("Class {} must be annotated with {}",
                    runnableObject.getClass().getCanonicalName(),
                    I18nResource.class.getCanonicalName());
        }
        final Step stepDefinition = runnableMethod.getAnnotation(Step.class);
        if (stepDefinition == null) {
            throw new KukumoException("Method {}::{} must be annotated with {}",
                    runnableObject.getClass().getCanonicalName(), runnableMethod.getName(), Step.class.getCanonicalName());
        }
        for (Class<?> methodArgumentType: runnableMethod.getParameterTypes()) {
            if (methodArgumentType.isPrimitive()) {
                throw new KukumoException("Method {}::{} must not use primitive argument type {}; use analogous boxed type",
                        runnableObject.getClass().getCanonicalName(), runnableMethod.getName(), methodArgumentType.getName());
            }
        }
        return new RunnableStep(
                stepDefinitionFile.value(),
                stepDefinition.value(),
                new BackendArguments(runnableObject.getClass(),runnableMethod,typeRegistry),
                (args -> runnableMethod.invoke(runnableObject,args))
        );
    }





}