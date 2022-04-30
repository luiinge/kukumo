/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package iti.kukumo.core.test.datatypes;


import iti.kukumo.core.backend.StepExpression;
import iti.kukumo.core.datatypes.*;
import iti.kukumo.plugin.api.DataTypes;
import java.util.*;
import java.util.regex.Matcher;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TestExpressionMatcher {

    static final DataTypes dataTypes = new DataTypes(List.of(
        new IntegerDataType(),
        new WordDataType(),
        new TextDataType()
    ));


    @ParameterizedTest
    @ValueSource(strings = {
        "que los siguientes datos se insertan en la tabla de BBDD USER:",
        "que el siguiente dato se inserta en la tabla de BBDD USER:",
        "que lo siguiente se inserta en la tabla de BBDD USER:",
        "los siguientes datos se insertan en la tabla de BBDD USER:",
        "lo siguiente se inserta en la tabla de BBDD USER:",
        "que siguiente se inserta en la tabla de BBDD USER:"
    })
    void testOptionalWithChoicesEs(String step) {
        var expression = "(que) el|la|lo|los|las siguiente(s) * se inserta(n) en la tabla de BBDD {word}:";
        var locale = new Locale("es");
        assertExpression(expression, locale, step);
    }


    @ParameterizedTest
    @ValueSource(strings = {
        "the following data is inserted in the database table USER:",
        "the following data are inserted in the database table USER:",
        "the following data inserted in the database table USER:",
        "the following is inserted in the database table USER:",
        "the following are inserted in the database table USER:",
        "the following inserted in the database table USER:",
        "the following inserted in the database table USER:"
    })
    void testOptionalWithChoicesEn(String step) {
        var expression = "(that) the following * (is|are) inserted in the database table {word}:";
        var locale = Locale.ENGLISH;
        assertExpression(expression, locale, step);
    }




    @ParameterizedTest
    @ValueSource(strings = {
        "un usuario identificado por '3'",
        "una usuaria identificada por '3'",
        "unos usuarios identificados por '3'",
        "unas usuarias identificadas por '3'",
        "identificado por '3'"
    })
    void testExpressionStep3(String step) {
        var expression = "* identificad(o|a|os|as) por {text}";
        var locale = new Locale("es");
        assertExpression(expression, locale, step);
    }


    @ParameterizedTest
    @ValueSource(strings = {
        "que el siguiente dato se inserta en la tabla de BBDD USER:",
        "que la siguiente cosa se inserta en la tabla de BBDD USER:",
        "que lo siguiente se inserta en la tabla de BBDD USER:",
        "que los siguientes datos se insertan en la tabla de BBDD USER:",
        "que las siguientes cosas se insertan en la tabla de BBDD USER:",
        "el siguiente dato se inserta en la tabla de BBDD USER:",
        "la siguiente cosa se inserta en la tabla de BBDD USER:",
        "lo siguiente se inserta en la tabla de BBDD USER:",
        "los siguientes datos se insertan en la tabla de BBDD USER:",
        "las siguientes cosas se insertan en la tabla de BBDD USER:"
    })
    void testExpressionStep4(String step) {
        assertExpression(
            "(que) el|la|lo|los|las siguiente(s) * se inserta(n) en la tabla de BBDD {word}:",
            new Locale("es"),
            step
        );
    }


    @ParameterizedTest
    @ValueSource(strings = {
        "se realiza la búsqueda",
        "se realiza la búsqueda de algo"
    })
    void testExpressionStep5(String step) {
        assertExpression(
            "se realiza la búsqueda *",
            new Locale("es"),
            step
        );
    }


    private void assertExpression(String expression, Locale locale, String step) {
        Matcher matcher = new StepExpression(
            expression,
            locale,
            locale,
            dataTypes
        ).matcherFor(step);
        assertTrue(matcher.matches(), "<<" + step + ">> not matching <<" + expression + ">>");
    }


}
