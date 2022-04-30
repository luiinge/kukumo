package iti.kukumo.plugin.api.lang;

@FunctionalInterface
public interface ThrowableRunnable {

    void run(Object... arguments) throws Exception;

}
