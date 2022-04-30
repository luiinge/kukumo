package iti.kukumo.plugin.api.plan.mutators;

import java.util.Optional;
import java.util.function.*;
import java.util.stream.Stream;
import iti.kukumo.plugin.api.plan.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class Mutator implements PlanNodeMutationRule {

    private final Predicate<MutablePlanNode> condition;
    private final Consumer<MutablePlanNode> action;
    private final Strategy strategy;


    @Override
    public void mutate(Stream<MutablePlanNode> nodes) {
        strategy.apply(nodes, condition, action);
    }


    public Mutator and(Predicate<MutablePlanNode> otherCondition) {
        return new Mutator(condition.and(otherCondition), action, strategy);
    }


    public Mutator or(Predicate<MutablePlanNode> otherCondition) {
        return new Mutator(condition.or(otherCondition), action, strategy);
    }


    public Mutator then(Consumer<MutablePlanNode> newAction) {
        return new Mutator(condition, newAction, strategy);
    }


    public Mutator andThen(Consumer<MutablePlanNode> otherAction) {
        return new Mutator(condition, action.andThen(otherAction), strategy);
    }


    public <C> ContextualMutator<C> given(Function<MutablePlanNode,C> context) {
        return new ContextualMutator<>(context,condition,action,strategy);
    }

    public <C> ContextualMutator<C> givenOptional(Function<MutablePlanNode, Optional<C>> context) {
        return new ContextualMutator<>(it->context.apply(it).orElse(null),condition,action,strategy);
    }

}
