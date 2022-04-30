package iti.kukumo.core.backend;

import static iti.kukumo.core.backend.StepExpression.UNNAMED;
import iti.kukumo.plugin.api.annotations.Param;
import iti.kukumo.plugin.api.plan.PlanNode;
import java.lang.reflect.Method;
import java.util.*;

public record StepMethodArgument(String name, Class<?> type, int position) {


    public static List<StepMethodArgument> fromMethod(Method method) {
        var arguments = new ArrayList<StepMethodArgument>();
        for (int i = 0; i < method.getParameterCount(); i++) {
            var parameter = method.getParameters()[i];
            var paramAnnotation = parameter.getAnnotation(Param.class);
            if (paramAnnotation == null) {
                arguments.add(new StepMethodArgument(parameter.getType(), i));
            } else {
                arguments.add(new StepMethodArgument(paramAnnotation.value(), parameter.getType(), i));
            }
        }
        return List.copyOf(arguments);
    }



    public StepMethodArgument(Class<?> type, int position) {
        this(UNNAMED,type,position);
    }


    public boolean isUnnamed() {
        return isRegularType() && UNNAMED.equals(name);
    }


    /**
     * Return <code>true</code> if the type is not a subtype of
     * {@link iti.kukumo.plugin.api.plan.PlanNode.PlanNodeArgument}
     */
    public boolean isRegularType() {
        return !PlanNode.PlanNodeArgument.class.isAssignableFrom(type);
    }


    /**
     * Return <code>true</code> if the type is a subtype of
     * {@link iti.kukumo.plugin.api.plan.PlanNode.PlanNodeArgument}
     */
    public boolean isNodeArgumentType() {
        return PlanNode.PlanNodeArgument.class.isAssignableFrom(type);
    }

}
