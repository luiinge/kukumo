package iti.kukumo.plugin.api.contributions;


import java.util.Optional;
import iti.kukumo.plugin.api.Contribution;
import iti.kukumo.plugin.api.plan.MutablePlanNode;
import iti.kukumo.plugin.api.sources.Source;
import org.jexten.ExtensionPoint;

@ExtensionPoint(version = "2.0")
public interface PlanAssembler extends Contribution {

    boolean accepts(ContentType<?> contentType);

    Optional<MutablePlanNode> assembleTestPlan(Source<?> input);


}
