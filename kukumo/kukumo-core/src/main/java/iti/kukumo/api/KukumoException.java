package iti.kukumo.api;

import java.util.Arrays;

public class KukumoException extends RuntimeException {

    private static final long serialVersionUID = 3126782976719868151L;


    public KukumoException() {
        super();
    }

    public KukumoException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public KukumoException(String message) {
        super(message);
    }

    public KukumoException(String message, Object... args) {
        super(replace(message,argsWithoutThrowable(args)),throwable(args));
    }

    public KukumoException(Throwable throwable) {
        super(throwable);
    }



    private static String replace(String message, Object[] args) {
        StringBuilder s = new StringBuilder(message);
        for (int i=0;i<args.length;i++) {
            int pos = s.indexOf("{}");
            if (pos == -1) {
                break;
            }
            s.replace(pos, pos+2, String.valueOf(args[i]));
        }
        return s.toString();
    }


    private static Object[] argsWithoutThrowable(Object[] args) {
        return throwable(args) == null ? args : Arrays.copyOf(args,args.length-1);
    }


    private static Throwable throwable(Object...args) {
        if (args == null || args.length == 0) {
            return null;
        }
        return args[args.length-1] instanceof Throwable ? (Throwable)args[args.length-1] : null;
    }

}