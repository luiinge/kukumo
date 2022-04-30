package iti.kukumo.core.exceptions;

import org.opentest4j.IncompleteExecutionException;

public class NonLocalizableStep extends IncompleteExecutionException {

    public NonLocalizableStep(String message, Object... arguments) {
        super(message.replace("{}","%s").formatted(arguments));
    }

}
