package iti.kukumo.core.test.util;

import iti.kukumo.core.util.LogicExpression;
import java.util.List;
import org.junit.jupiter.api.*;

class TestLogicExpression {

    @Test
    void singleExpressionEvaluatesLogicSuccessfully() {
        var expression = new LogicExpression("tagA");
        Assertions.assertFalse(expression.evaluate(List.of()));
        Assertions.assertTrue(expression.evaluate(List.of("tagA")));
        Assertions.assertFalse(expression.evaluate(List.of("tagB")));
        Assertions.assertTrue(expression.evaluate(List.of("tagA","tagB")));
    }


    @Test
    void notExpressionEvaluatesLogicSuccessfully() {
        var expression = new LogicExpression("not tagA");
        Assertions.assertTrue(expression.evaluate(List.of()));
        Assertions.assertFalse(expression.evaluate(List.of("tagA")));
        Assertions.assertTrue(expression.evaluate(List.of("tagB")));
        Assertions.assertFalse(expression.evaluate(List.of("tagA","tagB")));
    }


    @Test
    void andExpressionEvaluatesLogicSuccessfully() {
        var expression = new LogicExpression("tagA and tagB");
        Assertions.assertFalse(expression.evaluate(List.of()));
        Assertions.assertFalse(expression.evaluate(List.of("tagA")));
        Assertions.assertFalse(expression.evaluate(List.of("tagB")));
        Assertions.assertTrue(expression.evaluate(List.of("tagA","tagB")));
    }


    @Test
    void orExpressionEvaluatesLogicSuccessfully() {
        var expression = new LogicExpression("tagA or tagB");
        Assertions.assertFalse(expression.evaluate(List.of()));
        Assertions.assertTrue(expression.evaluate(List.of("tagA")));
        Assertions.assertTrue(expression.evaluate(List.of("tagB")));
        Assertions.assertTrue(expression.evaluate(List.of("tagA","tagB")));
        Assertions.assertTrue(expression.evaluate(List.of("tagA","tagC")));
        Assertions.assertTrue(expression.evaluate(List.of("tagC","tagB")));
        Assertions.assertFalse(expression.evaluate(List.of("tagC")));
    }


    @Test
    void xorExpressionEvaluatesLogicSuccessfully() {
        var expression = new LogicExpression("tagA xor tagB");
        Assertions.assertFalse(expression.evaluate(List.of()));
        Assertions.assertTrue(expression.evaluate(List.of("tagA")));
        Assertions.assertTrue(expression.evaluate(List.of("tagB")));
        Assertions.assertFalse(expression.evaluate(List.of("tagA","tagB")));
        Assertions.assertTrue(expression.evaluate(List.of("tagA","tagC")));
        Assertions.assertTrue(expression.evaluate(List.of("tagC","tagB")));
        Assertions.assertFalse(expression.evaluate(List.of("tagC")));
    }


    @Test
    void complexExpressionEvaluatesLogicSuccessfully() {
        var expression = new LogicExpression("tagA and not (tagB or tagC)");
        Assertions.assertFalse(expression.evaluate(List.of()));
        Assertions.assertTrue(expression.evaluate(List.of("tagA")));
        Assertions.assertFalse(expression.evaluate(List.of("tagB")));
        Assertions.assertFalse(expression.evaluate(List.of("tagC")));
        Assertions.assertFalse(expression.evaluate(List.of("tagA","tagB")));
        Assertions.assertFalse(expression.evaluate(List.of("tagA","tagC")));
        Assertions.assertFalse(expression.evaluate(List.of("tagC","tagB")));
        Assertions.assertTrue(expression.evaluate(List.of("tagA","tagD")));
    }


    @Test
    void invalidExpressionThrowsExceptionWhenEvaluated() {
        Assertions.assertThrowsExactly(
            IllegalArgumentException.class,
            () -> new LogicExpression("tagA xand tagB").evaluate(List.of()),
             "Invalid logic expression: tagA xand tagB"
        );
    }
}
