package iti.kukumo.core.backend;

import java.lang.reflect.*;

public record MethodInvocation(Object object, Method method) {

    public void run(Object... arguments) throws InvocationTargetException, IllegalAccessException {
        method.invoke(object,arguments);
    }

}
