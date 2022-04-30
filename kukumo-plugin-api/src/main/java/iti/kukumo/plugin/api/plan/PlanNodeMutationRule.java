package iti.kukumo.plugin.api.plan;

import java.util.stream.Stream;

@FunctionalInterface
public interface PlanNodeMutationRule {

    void mutate(Stream<MutablePlanNode> nodes);

    default void mutate(MutablePlanNode node) {
        mutate(node.stream());
    }

}
