package iti.kukumo.plugin.api.plan.mutators;

import iti.kukumo.plugin.api.lang.Functions;
import static iti.kukumo.plugin.api.lang.Functions.*;
import java.util.function.*;
import java.util.stream.Stream;
import iti.kukumo.plugin.api.plan.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ContextualMutator<C> implements PlanNodeMutationRule {

    private final Function<MutablePlanNode,C> context;
    private final Predicate<MutablePlanNode> condition;
    private final Consumer<MutablePlanNode> action;
    private final Strategy strategy;


    @Override
    public void mutate(Stream<MutablePlanNode> nodes) {
        strategy.apply(nodes, condition, action);
    }


    public ContextualMutator<C> and(Predicate<MutablePlanNode> otherCondition) {
        return new ContextualMutator<>(context, condition.and(otherCondition), action, strategy);
    }


    public ContextualMutator<C> and(BiPredicate<MutablePlanNode,C> otherCondition) {
        return new ContextualMutator<>(
            context,
            condition.and(contextualCondition(context, otherCondition)),
            action,
            strategy
        );
    }


    public ContextualMutator<C> or(Predicate<MutablePlanNode> otherCondition) {
        return new ContextualMutator<>(context, condition.or(otherCondition), action, strategy);
    }


    public ContextualMutator<C> or(BiPredicate<MutablePlanNode,C> otherCondition) {
        return new ContextualMutator<>(
            context,
            condition.or(contextualCondition(context, otherCondition)),
            action,
            strategy
        );
    }


    public ContextualMutator<C> then(Consumer<MutablePlanNode> newAction) {
        return new ContextualMutator<>(context, condition, newAction, strategy);
    }


    public ContextualMutator<C> then(BiConsumer<MutablePlanNode,C> newAction) {
        return new ContextualMutator<>(
            context,
            condition,
            contextualAction(context, newAction),
            strategy
        );
    }


    public ContextualMutator<C> andThen(Consumer<MutablePlanNode> otherAction) {
        return new ContextualMutator<>(context, condition, action.andThen(otherAction), strategy);
    }


    public ContextualMutator<C> andThen(BiConsumer<MutablePlanNode,C> otherAction) {
        return new ContextualMutator<>(
            context,
            condition,
            action.andThen(contextualAction(context, otherAction)),
            strategy
        );
    }


    private static <C> Predicate<MutablePlanNode> contextualCondition(
        Function<MutablePlanNode,C> contextSupplier,
        BiPredicate<MutablePlanNode,C> condition
    ) {
        return t -> Functions.or(let(contextSupplier.apply(t), c -> condition.test(t, c)),false);
    }


    private static <C> Consumer<MutablePlanNode> contextualAction(
        Function<MutablePlanNode,C> contextSupplier,
        BiConsumer<MutablePlanNode,C> action
    ) {
        return t -> also(contextSupplier.apply(t), c -> action.accept(t, c));
    }


}
