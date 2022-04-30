package iti.kukumo.plugin.api.contributions;

import iti.kukumo.plugin.api.Contribution;
import iti.kukumo.plugin.api.plan.*;
import org.jexten.ExtensionPoint;

@ExtensionPoint(version = "2.0")
public interface PlanExecutionProcessor extends Contribution {

    void processExecution(PlanExecution execution);

}
