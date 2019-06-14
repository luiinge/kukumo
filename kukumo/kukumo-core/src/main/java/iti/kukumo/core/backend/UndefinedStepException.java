package iti.kukumo.core.backend;

import iti.kukumo.api.KukumoException;
import iti.kukumo.api.plan.PlanStep;

public class UndefinedStepException extends KukumoException {

    private static final long serialVersionUID = 5923513040489649029L;

    public UndefinedStepException(PlanStep step, String message, String extraInfo) {
        super("Cannot match step at <{}> '{}' : {}\n{}", step.source(),step.displayName(),message,extraInfo);
    }
    
   


}