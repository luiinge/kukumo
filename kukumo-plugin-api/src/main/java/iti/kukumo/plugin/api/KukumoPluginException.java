package iti.kukumo.plugin.api;

public class KukumoPluginException extends RuntimeException {

    public KukumoPluginException(String message) {
        super(message);
    }

    public KukumoPluginException(String message, Object... args) {
        super(format(message,args));
    }

    public KukumoPluginException(Exception e, String message, Object... args) {
        super(format(message,args)+" :: "+e.getMessage(),e);
    }


    public KukumoPluginException(Exception e) {
        super(e.getMessage(),e);
    }


    private static String format(String message, Object[] args) {
        return message.replace("{}","%s").formatted(args);
    }

}
