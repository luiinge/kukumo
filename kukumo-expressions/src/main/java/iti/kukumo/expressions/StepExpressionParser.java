package iti.kukumo.expressions;


import iti.kukumo.expressions.internal.DefaultStepExpressionParser;

public interface StepExpressionParser {

    static StepExpression parse(String expression) throws ExpressionParsingException {
        return DefaultStepExpressionParser.parse(expression);
    }

}
