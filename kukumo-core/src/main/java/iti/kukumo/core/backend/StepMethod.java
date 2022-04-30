package iti.kukumo.core.backend;

import iti.kukumo.core.exceptions.InvalidStepMethod;
import iti.kukumo.plugin.api.contributions.DataType;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public record StepMethod(
    Object stepContribution,
    StepExpression expression,
    Method stepMethod,
    List<StepMethodArgument> stepArguments
) {

    public StepMethod(
        Object stepContribution,
        StepExpression expression,
        Method stepMethod
    ) throws InvalidStepMethod{
        this(stepContribution, expression, stepMethod, stepArguments(stepMethod,expression) );
    }


    public void run(Object... arguments) throws InvocationTargetException, IllegalAccessException {
        stepMethod.invoke(stepContribution,arguments);
    }



    private static List<StepMethodArgument> stepArguments(Method method, StepExpression expression)
        throws InvalidStepMethod {

        var arguments = StepMethodArgument.fromMethod(method);
        int count = arguments.size();


        if (arguments.stream().anyMatch(it -> it.isNodeArgumentType() && it.position() != count-1)) {
            throw new InvalidStepMethod(method, expression,
                "Argument of type PlanNodeArgument must be in the last position"
            );
        }

        if (arguments.stream().filter(StepMethodArgument::isUnnamed).count() > 1) {
            throw new InvalidStepMethod(method, expression,
                "Arguments should be annotated with @Param"
            );
        }

        if (arguments.stream().map(StepMethodArgument::name).distinct().count() != count) {
            throw new InvalidStepMethod(method, expression,
                "Cannot be more than one @Param with the same name",
                method.getName()
            );
        }

        boolean hasPlanNodeArgument = arguments.stream().anyMatch(StepMethodArgument::isNodeArgumentType);
        Map<String, DataType<?>> expressionArguments = expression.arguments();
        int expectedMethodArguments = expressionArguments.size() + (hasPlanNodeArgument ? 1 : 0);
        if (expectedMethodArguments != count) {
            throw new InvalidStepMethod(method, expression,
                "Expected {} arguments instead of {}",
                expectedMethodArguments,
                count
            );
        }


        var indexedArguments = arguments.stream().collect(Collectors.toMap(StepMethodArgument::name, e->e));
        expression.arguments().forEach((param,dataType)->{

            if (!indexedArguments.containsKey(param)) {
                throw new InvalidStepMethod(method, expression,
                    "Expected one argument annotated with @Param(\"{}\")",
                    param
                );
            }

            if (indexedArguments.get(param).type() != dataType.javaType()) {
                throw new InvalidStepMethod(method, expression,
                    "Argument annotated with @Param(\"{}\") should be of type {} instead of {}",
                    param,
                    dataType,
                    indexedArguments.get(param).type()
                );
            }

        });

        return arguments;
    }

}
