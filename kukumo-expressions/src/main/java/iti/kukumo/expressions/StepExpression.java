package iti.kukumo.core.expressions;

import iti.kukumo.core.expressions.internal.*;

public interface StepExpression {

    static StepExpression parse(String step) {
        return DefaultStepExpressionParser.parse(step);
    }

    ExpressionMatcher match(ExpressionMatcher.ExpressionMatchBuilder match);


}
