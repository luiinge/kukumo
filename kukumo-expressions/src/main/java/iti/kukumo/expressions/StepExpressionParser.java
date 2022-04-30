package iti.kukumo.core.expressions;

import iti.kukumo.core.expressions.internal.DefaultStepExpressionParser;

public interface StepExpressionParser {

    static StepExpression parse(String expression) throws ExpressionParsingException {
        return DefaultStepExpressionParser.parse(expression);
    }

}
