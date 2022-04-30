package iti.kukumo.expressions;


import iti.kukumo.expressions.internal.*;

public interface StepExpression {

    static StepExpression parse(String step) {
        return DefaultStepExpressionParser.parse(step);
    }

    ExpressionMatch match(ExpressionMatchBuilder match);


}
