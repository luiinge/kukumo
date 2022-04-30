package iti.kukumo.core.exceptions;

import iti.kukumo.core.backend.StepExpression;
import java.lang.reflect.Method;

public class InvalidStepMethod extends WrongStepDefinition{

    public InvalidStepMethod(Method method, String message, Object... arguments) {
        super(
            "Invalid step method %s.%s :: ".formatted(
                method.getDeclaringClass().getSimpleName(),
                method.getName()
            ) + message,
            arguments
        );
    }

    public InvalidStepMethod(Method method, StepExpression expression, String message, Object... arguments) {
        super(
            "Invalid step method %s.%s :: ".formatted(
                method.getDeclaringClass().getSimpleName(),
                method.getName()
            ) + message + "\n    Step expression was "+expression,
            arguments
        );
    }

}
