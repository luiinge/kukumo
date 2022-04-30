package iti.kukumo.plugin.api.contributions;

import java.util.List;
import iti.kukumo.plugin.api.Contribution;
import iti.kukumo.plugin.api.plan.PlanNodeMutationRule;
import org.jexten.ExtensionPoint;

@ExtensionPoint(version = "2.0")
public interface PlanMutationRules extends Contribution {

    List<PlanNodeMutationRule> rules();

}
