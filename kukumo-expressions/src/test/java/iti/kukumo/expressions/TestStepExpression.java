package iti.kukumo.core.test.expressions;



import iti.kukumo.core.datatypes.DecimalDataType;
import iti.kukumo.core.expressions.*;
import iti.kukumo.core.expressions.internal.ExpressionMatcher;
import iti.kukumo.plugin.api.DataTypes;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TestStepExpression {


    private static final DataTypes dataTypes = new DataTypes(List.of(new DecimalDataType()));

    private static ExpressionMatcher.ExpressionMatchBuilder builder (String step) {
        return ExpressionMatcher.builder(step).locale(Locale.ENGLISH).dataTypes(dataTypes);
    }

    // expression:  the following is inserted in the database
    static Stream<Arguments> simpleExpression() {
        return Stream.of(
            arguments("the following is inserted in the database",true),
            arguments("tho following is inserted in the database",false),
            arguments("  the following  is inserted in the database  ",true),
            arguments("  the \t following   is inserted in the database  ",true),
            arguments("  tho following  is inserted in the database  ",false),
            arguments("The Following is inserted in the database",true),
            arguments("Tho Following is inserted in the database",false)
        );
    }


    @ParameterizedTest
    @MethodSource("simpleExpression")
    @DisplayName("simple expression 'the following is inserted in the database'")
    void expressionMatch(String step, boolean match) {
        String exp = "the following is inserted in the database";
        assertThat(
            StepExpression.parse(exp).match(builder(step)).matches()
        ).isEqualTo(match);
    }


    // expression:  the following is|are inserted in [the database|the data warehouse|the data bank]
    static Stream<Arguments> choiceExpression() {
        return Stream.of(
            arguments("the following is inserted in the database",true),
            arguments("the following are inserted in the database",true),
            arguments("  the following  is inserted in the database  ",true),
            arguments("  the \t following   is inserted in the database  ",true),
            arguments("  the following  are inserted in the database  ",true),
            arguments("  the \t following   are  inserted in the database  ",true),
            arguments("the following Is inserted in the database",true),
            arguments("the following Are inserted in the database",true),
            arguments("the following iss inserted in the database",false),
            arguments("the following are not inserted in the database",false),

            arguments("the following is inserted in the data warehouse",true),
            arguments("the following are inserted in the data warehouse",true),
            arguments("  the following  is inserted in the data warehouse  ",true),
            arguments("  the \t following   is inserted in the data warehouse  ",true),
            arguments("  the following  are inserted in the data warehouse  ",true),
            arguments("  the \t following   are  inserted in the data warehouse  ",true),
            arguments("the following Is inserted in the data warehouse",true),
            arguments("the following Are inserted in the data warehouse",true),
            arguments("the following iss inserted in the data warehouse",false),
            arguments("the following are not inserted in the data warehouse",false),

            arguments("the following is inserted in the data bank",true),
            arguments("the following are inserted in the data bank",true),
            arguments("  the following  is inserted in the data bank  ",true),
            arguments("  the \t following   is inserted in the data bank  ",true),
            arguments("  the following  are inserted in the data bank  ",true),
            arguments("  the \t following   are  inserted in the data bank  ",true),
            arguments("the following Is inserted in the data bank",true),
            arguments("the following Are inserted in the data bank",true),
            arguments("the following iss inserted in the data bank",false),
            arguments("the following are not inserted in the data bank",false),

            arguments("the following is inserted in the data",false),
            arguments("the following are inserted in the data",false)

        );
    }


    @ParameterizedTest
    @MethodSource("choiceExpression")
    @DisplayName("choice expression 'the following is|are inserted in [the database|the data warehouse|the data bank]'")
    void choiceExpressionMatch(String step, boolean match) {
        String exp = "the following is|are inserted in [the database|the data warehouse|the data bank]";
        assertThat(
            StepExpression.parse(exp).match(builder(step)).matches()
        ).isEqualTo(match);
    }


    // expression:  the following (data) is insert(ed) in the (pretty large) database
    static Stream<Arguments> optionalExpression() {
        return Stream.of(
            arguments("the following is insert in the database",true),
            arguments("the following data is insert in the database",true),
            arguments("the following data is inserted in the database",true),
            arguments("the following is inserted in the database",true),
            arguments("the following is insert in the pretty large database",true),
            arguments("the following data is insert in the pretty large database",true),
            arguments("the following data is inserted in the pretty large database",true),
            arguments("the following is inserted in the pretty large database",true),
            arguments("the following is insert in the pretty database",false),
            arguments("the following stuff is inserted in the database",false),
            arguments("the following is insertod in the database",false)
        );
    }


    @ParameterizedTest
    @MethodSource("optionalExpression")
    @DisplayName("optional expression 'the following (data) is insert(ed) in the (pretty large) database'")
    void optionalExpressionMatch(String step, boolean match) {
        String exp = "the following (data) is insert(ed) in the (pretty large) database";
        assertThat(
            StepExpression.parse(exp).match(builder(step)).matches()
        ).isEqualTo(match);
    }


    // expression:  the following ^data is inserted in the ^[empty database]
    static Stream<Arguments> negatedExpression() {
        return Stream.of(
            arguments("the following thing is inserted in the database",true),
            arguments("the following thing is inserted in the empty warehouse",true),
            arguments("the following data is inserted in the database",false),
            arguments("the following thing is inserted in the empty database",false)
        );
    }


    @ParameterizedTest
    @MethodSource("negatedExpression")
    @DisplayName("negated expression 'the following ^data is inserted in the ^[empty database]'")
    void negatedExpressionMatch(String step, boolean match) {
        String exp = "the following ^data is inserted in the ^[empty database]";
        assertThat(
            StepExpression.parse(exp).match(builder(step)).matches()
        ).isEqualTo(match);
    }



    // expression:  the following (is|are) inserted in (the pretty | a large) database
    static Stream<Arguments> optionalChoiceExpression() {
        return Stream.of(
            arguments("the following is inserted in the pretty database",true),
            arguments("the following is inserted in a large database",true),
            arguments("the following is inserted in database",true),
            arguments("the following are inserted in the pretty database",true),
            arguments("the following are inserted in a large database",true),
            arguments("the following are inserted in database",true),
            arguments("the following inserted in the pretty database",true),
            arguments("the following inserted in a large database",true),
            arguments("the following inserted in database",true),

            arguments("the following was inserted in the pretty database",false),
            arguments("the following was inserted in a large database",false),
            arguments("the following was inserted in database",false),
            arguments("the following is inserted in the database",false),
            arguments("the following is inserted in a database",false),
            arguments("the following is inserted in some empty database",false)

        );
    }


    @ParameterizedTest
    @MethodSource("optionalChoiceExpression")
    @DisplayName("optional choice expression 'the following (is|are) inserted in (the pretty | a large) database'")
    void optionalChoiceExpressionMatch(String step, boolean match) {
        String exp = "the following (is|are) inserted in (the pretty | a large) database";
        assertThat(
            StepExpression.parse(exp).match(builder(step)).matches()
        ).isEqualTo(match);
    }


    // expression:  the following * is inserted in the database
    static Stream<Arguments> wildcardExpression() {
        return Stream.of(
            arguments("the following thing is inserted in the database",true),
            arguments("the following nice stuff is inserted in the database",true),
            arguments("the following is inserted in the database",true)
        );
    }


    @ParameterizedTest
    @MethodSource("wildcardExpression")
    @DisplayName("wildcard expression 'the following * is inserted in the database'")
    void wildcardExpressionMatch(String step, boolean match) {
        String exp = "the following * is inserted in the database";
        assertThat(
            StepExpression.parse(exp).match(builder(step)).matches()
        ).isEqualTo(match);
    }



    @Test
    @DisplayName("escaped expression 'the following \\{int\\} \\(is\\|are\\) inserted in the database'")
    void escapedExpressionMatch() {
        String exp = "the following \\{int\\} \\(is\\|are\\) inserted in the database";
        String step = "the following {int} (is|are) inserted in the database";
        assertThat(
            StepExpression.parse(exp).match(builder(step)).matches()
        ).isTrue();
    }



    // expression:  the following thing is equal to {decimal} and nothing more'
    static Stream<Arguments> argumentExpression() {
        return Stream.of(
            arguments("the following thing is equal to 3.7 and nothing more",true, new BigDecimal("3.7")),
            arguments("the following thing is equal to 3.7",false, null),
            arguments("the following thing is equal to dog",false, null)
        );
    }


    @ParameterizedTest
    @MethodSource("argumentExpression")
    @DisplayName("argument expression 'the following * is equal to {decimal} and nothing more'")
    void argumentExpressionMatch(String step, boolean match, BigDecimal value) {
        String exp = "the following thing is equal to {decimal} and nothing more";
        var expressionMatch = StepExpression.parse(exp).match(builder(step));
        assertThat(expressionMatch.matches()).isEqualTo(match);
        if (expressionMatch.matches()) {
            assertThat(expressionMatch.argument("decimal").javaValue()).isEqualTo(value);
        }
    }




}
