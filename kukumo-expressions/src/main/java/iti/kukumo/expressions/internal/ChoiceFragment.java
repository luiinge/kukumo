package iti.kukumo.expressions.internal;

import java.util.Collections;
import java.util.stream.Collectors;


final class ChoiceFragment extends CompoundFragment implements RegexContributor {

    ChoiceFragment() {
        //
    }


    ChoiceFragment(StepExpressionFragment... nodes) {
        Collections.addAll(this.fragments, nodes);
    }

    ChoiceFragment(Iterable<StepExpressionFragment> nodes) {
       nodes.forEach(this::add);
    }


    @Override
    protected StepExpressionFragment normalized() {
        if (fragments.isEmpty()) {
            return null;
        } else if (fragments.size() == 1) {
            return fragments.getFirst().normalized();
        } else {
            return this;
        }
    }



    @Override
    public String regex() {
        return fragments.stream()
            .filter(RegexContributor.class::isInstance)
            .map(RegexContributor.class::cast)
            .map(RegexContributor::regex)
            .collect(Collectors.joining("|","(",")"));
    }


}
