package iti.kukumo.expressions.internal;


import iti.kukumo.expressions.*;

interface EvaluableFragment extends StepExpression {

    boolean consumeFragment(ExpressionMatcher match);


    @Override
    default ExpressionMatcher match(ExpressionMatchBuilder matchBuilder) {
        ExpressionMatcher match = (ExpressionMatcher) matchBuilder.build();
        consumeFragment(match);
        return match;
    }


}
