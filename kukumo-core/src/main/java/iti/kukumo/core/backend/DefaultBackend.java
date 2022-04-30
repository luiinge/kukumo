package iti.kukumo.core.backend;

import imconfig.Config;
import iti.kukumo.core.ExecutablePlanNode;
import iti.kukumo.core.exceptions.*;
import iti.kukumo.plugin.api.*;
import iti.kukumo.plugin.api.annotations.*;
import iti.kukumo.plugin.api.contributions.*;
import iti.kukumo.plugin.api.plan.NodeType;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;
import org.jexten.ExtensionManager;
import org.opentest4j.*;


public class DefaultBackend implements Backend {

    private final Log log = Log.of();
    private final ExecutionContext context;
    private final DataTypes dataTypes;
    private final Map<Class<? extends StepContribution>, StepLocalizer> localizers;
    private final List<MethodInvocation> setUp;
    private final List<MethodInvocation> tearDown;
    private final List<StepMethod> steps;
    private final BackendHelper helper;
    private final Map<ExecutablePlanNode,StepMethod> matchingStepMethods = new HashMap<>();
    private final Map<ExecutablePlanNode,Throwable> preExecutionErrors = new HashMap<>();


    public DefaultBackend(
        Config executionConfig,
        ExecutablePlanNode testCaseNode,
        ExtensionManager extensionManager,
        Contributions contributions
    ) {
        this.context = new ExecutionContext(
            testCaseNode.snapshot(),
            executionConfig.append(testCaseNode.configuration()),
            contributions
        );
        var testExtensionManger = extensionManager.withInjectionProvider((type,name)->{
            if (type == Config.class && name.equals("testConfiguration"))
                return Stream.of(context.config());
            if (type == Config.class)
                return Stream.of(executionConfig);
            if (type == ExecutionContext.class)
                return Stream.of(context);
            if (type == Localizer.class)
                return Stream.of(contributions.localizer());
            if (type == DataTypes.class)
                return Stream.of(contributions.dataTypes());
            return Stream.of();
        });

        var stepsFilter = context.config().getList(KukumoProperties.STEPS);
         var stepContributions = stepContributions(testExtensionManger, stepsFilter);
        this.localizers = stepContributions.stream().collect(Collectors.toMap(
            StepContribution::getClass,
            it -> new StepLocalizer(it, context.contributions().localizer()))
        );
        this.setUp = annotatedMethods(stepContributions, SetUp.class);
        this.tearDown = annotatedMethods(stepContributions, TearDown.class);
        this.steps = stepContributions.stream().flatMap(it->collectSteps(it).stream()).toList();
        this.dataTypes = context.contributions().dataTypes();
        this.helper = new BackendHelper(context, localizers, dataTypes);

        preExecutionCompilation(testCaseNode);

    }



    private List<StepMethod> collectSteps(StepContribution stepContribution) {

        var localizer = localizers.get(stepContribution.getClass());
        List<StepMethod> backendSteps = new ArrayList<>();

        var stepMethods = Stream.of(stepContribution.getClass().getMethods())
            .filter(it -> it.isAnnotationPresent(Step.class))
            .toList();

        for (var stepMethod : stepMethods) {
            try {
                var definitionKey = stepMethod.getAnnotation(Step.class).value();
                var definition = localizer.localize(definitionKey, context.stepLocale())
                    .orElseThrow( () -> new WrongStepDefinition(
                        "No localization found for step key {} (at {}:{}) using dataLocale {}",
                        definitionKey,
                        stepContribution.getClass().getSimpleName(),
                        stepMethod.getName(),
                        context.stepLocale()
                    ));
                var expression = new StepExpression(
                    definition,
                    context.stepLocale(),
                    context.dataLocale(),
                    dataTypes
                );
                backendSteps.add(new StepMethod(
                    stepContribution,
                    expression,
                    stepMethod
                ));
            } catch (WrongStepDefinition e) {
                log.error(e);
            }
        }

         return backendSteps;

    }



    private void preExecutionCompilation(ExecutablePlanNode node) {

        if (node.nodeType() == NodeType.STEP) {

            List<StepMethod> matchingSteps = steps.stream()
                .filter(it -> it.expression().matches(node.name()))
                .toList();

            if (matchingSteps.isEmpty()) {
                preExecutionErrors.put(node, new NonLocalizableStep("""
                    Step not matching any existing step definition.
                        {}
                        ---
                        Maybe you aimed to match one of the following:
                        {}
                    """,
                    node.name(),
                    helper.suggestionsForInvalidStep(node.name(), 5, true)
                ));

            } else if (matchingSteps.size() > 1) {
                preExecutionErrors.put(node, new NonLocalizableStep("""
                    Step matches more than one step definition.
                        {}
                        ---
                        Matching definitions were:
                        {}
                        ---
                        Consider using the property '{}' to restrict the step contributions applied to the test
                    """,
                    node.name(),
                    matchingSteps.stream().map(it -> "    - "+it).collect(Collectors.joining("\n")),
                    KukumoProperties.STEPS
                ));

            } else {
                matchingStepMethods.put(node, matchingSteps.get(0));
            }
        }
        node.children().forEach(this::preExecutionCompilation);
    }






    @Override
    public void setUp() throws TestAbortedException {
        try {
            for (var methodInvocation : setUp) {
                methodInvocation.run();
            }
        } catch (InvocationTargetException e) {
            throw new TestAbortedException("Error during test set-up", e.getTargetException());
        } catch (IllegalAccessException e) {
            throw new TestAbortedException("Error during test set-up", e);
        }
    }



    @Override
    public void tearDown() throws TestAbortedException{
        try {
            for (var methodInvocation : tearDown) {
                methodInvocation.run();
            }
        } catch (InvocationTargetException e) {
            throw new TestAbortedException("Error during test tear-down", e.getTargetException());
        } catch (IllegalAccessException e) {
            throw new TestAbortedException("Error during test tear-down", e);
        }
    }


    @Override
    public void executeStep(ExecutablePlanNode node) throws Throwable {
        if (!preExecutionErrors.isEmpty()) {
            var error = preExecutionErrors.get(node);
            if (error != null) {
                throw error;
            } else {
                throw new TestSkippedException();
            }
        }
        executeStepMethod(matchingStepMethods.get(node), node);
    }



    private void executeStepMethod(StepMethod step, ExecutablePlanNode node) throws Throwable {
        StepExpression expression = step.expression();
        Matcher matcher = expression.matcherFor(node.name());
        Object[] invocationArguments = new Object[step.stepArguments().size()];
        for (StepMethodArgument stepArgument : step.stepArguments()) {
            if (stepArgument.isRegularType()) {
                var name = stepArgument.name();
                invocationArguments[stepArgument.position()] = expression.parseArgument(
                    name,
                    matcher.group(name)
                );
            } else {
                invocationArguments[stepArgument.position()] = node.argument();
            }
        }
        try {
            step.run(invocationArguments);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (IllegalAccessException e) {
            throw new TestAbortedException("Cannot execute step!", e);
        }
    }


    private List<StepContribution> stepContributions(
        ExtensionManager extensionManager,
        List<String> stepsFilter
    ) {
        if (stepsFilter.isEmpty()) {
            return extensionManager.getExtensions(StepContribution.class)
                .filter(this::hasLocalization)
                .toList();
        } else {
            return extensionManager.getExtensionsByName(StepContribution.class, stepsFilter::contains)
                .filter(this::hasLocalization)
                .toList();
        }
    }



    private boolean hasLocalization(StepContribution stepContribution) {
        if (stepContribution.getClass().isAnnotationPresent(LocalizableWith.class)) {
            return true;
        } else {
            log.warn(
            "Step contribution {} is not localizable ({} annotation is absent) and will be ignored",
            stepContribution.getClass().getCanonicalName(),
            LocalizableWith.class.getCanonicalName()
            );
            return false;
        }
    }




    private static <A extends Annotation> List<MethodInvocation> annotatedMethods(
        List<StepContribution> stepContributions,
        Class<A> annotation
    ) {
        List<MethodInvocation> runnables = new ArrayList<>();
        for (StepContribution stepContribution : stepContributions) {
            for (Method method : stepContribution.getClass().getMethods()) {
                if (method.isAnnotationPresent(annotation) && method.getParameterCount() == 0) {
                    runnables.add(new MethodInvocation(stepContribution, method));
                }
            }
        }
        return runnables;
    }





}
