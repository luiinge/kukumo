package iti.kukumo.plugin.api.plan.mutators;

import java.util.function.Predicate;
import iti.kukumo.plugin.api.plan.MutablePlanNode;


public final class Mutators {

    private Mutators() { }


    private static Strategy applyToEach() {
        return ((elements, filter, action) -> elements.filter(filter).forEach(action));
    }


    public static Mutator forEach() {
        return new Mutator(it->true,it->{},applyToEach());
    }

    public static Mutator forEach(Predicate<MutablePlanNode> predicate) {
        return new Mutator(predicate,it->{},applyToEach());
    }


}
