package iti.kukumo.core.expressions.internal;


import iti.kukumo.core.expressions.*;

interface EvaluableFragment extends StepExpression {

    boolean consumeFragment(ExpressionMatcher match);


    @Override
    default ExpressionMatcher match(ExpressionMatcher.ExpressionMatchBuilder matchBuilder) {
        ExpressionMatcher match = matchBuilder.build();
        consumeFragment(match);
        return match;
    }


}
