package iti.kukumo.plugin.api.plan.mutators;

import java.util.function.*;
import java.util.stream.Stream;
import iti.kukumo.plugin.api.plan.MutablePlanNode;

@FunctionalInterface
interface Strategy {

    void apply(
        Stream<MutablePlanNode> elements,
        Predicate<MutablePlanNode> filter,
        Consumer<MutablePlanNode> action
    );

}
