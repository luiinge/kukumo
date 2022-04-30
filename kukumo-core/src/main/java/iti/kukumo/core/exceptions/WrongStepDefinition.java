package iti.kukumo.core.exceptions;

import org.opentest4j.IncompleteExecutionException;

public class WrongStepDefinition extends IncompleteExecutionException {

    public WrongStepDefinition(String message, Object... arguments) {
        super(message.replace("{}","%s").formatted(arguments));
    }

}
