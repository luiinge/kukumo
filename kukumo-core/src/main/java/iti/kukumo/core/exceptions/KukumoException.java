package iti.kukumo.core.exceptions;

public class KukumoException extends RuntimeException {


    public KukumoException(String message) {
        super(message);
    }

    public KukumoException(String message, Object... args) {
        super(format(message,args));
    }

    public KukumoException(Exception e, String message, Object... args) {
        super(format(message,args)+" :: "+e.getMessage(),e);
    }


    public KukumoException(Exception e) {
        super(e.getMessage(),e);
    }


    private static String format(String message, Object[] args) {
        return message.replace("{}","%s").formatted(args);
    }

}
