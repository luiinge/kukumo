package iti.kukumo.core.expressions.internal;

import iti.kukumo.core.expressions.*;
import java.util.*;


final class SequenceFragment extends CompoundFragment implements EvaluableFragment, StepExpression {

    SequenceFragment() {
        //
    }


    SequenceFragment(Iterable<StepExpressionFragment> nodes) {
        super(nodes);
    }

    SequenceFragment(StepExpressionFragment... nodes) {
        super(Arrays.asList(nodes));
    }


    @Override
    void add(StepExpressionFragment node) {
        if (node == null) {
            return;
        }
        if (size() > 0 && last() instanceof LiteralFragment last && node instanceof LiteralFragment literal) {
            this.fragments.removeLast();
            fragments.add(new LiteralFragment(last.value() + " " + literal.value()));
        } else {
            fragments.add(node);
        }
    }



    @Override
    protected StepExpressionFragment normalized() {
        if (size() == 1) {
            if (fragments.getFirst() instanceof SequenceFragment subsequence && subsequence.size() == 1) {
                return subsequence.first();
            } else {
                return fragments.getFirst();
            }
        } else {
            List<StepExpressionFragment> normal = new LinkedList<>();
            List<RegexContributor> regexContributors = new LinkedList<>();
            for (var fragment : fragments) {
                var normalized = fragment.normalized();
                if (normalized instanceof RegexContributor regex) {
                    regexContributors.add(regex);
                } else if (regexContributors.isEmpty()) {
                    normal.add(normalized);
                } else {
                    normal.add(new RegexFragment(regexContributors));
                    normal.add(normalized);
                    regexContributors.clear();
                }
            }
            if (!regexContributors.isEmpty()) {
                normal.add(new RegexFragment(regexContributors));
            }
            return normal.size() == 1 ? normal.get(0) : new SequenceFragment(normal);
        }
    }


    @Override
    public boolean consumeFragment(ExpressionMatcher match) {
        for (var fragment : fragments) {
            if (fragment instanceof EvaluableFragment evaluableFragment) {
                if (!evaluableFragment.consumeFragment(match)) {
                    return false;
                }
                if (match.pendingChars().startsWith(" ")) {
                    match.consume(1);
                }
            }
        }
        return true;
    }


}
