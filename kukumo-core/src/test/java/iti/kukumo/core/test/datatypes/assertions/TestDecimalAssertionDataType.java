/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package iti.kukumo.core.test.datatypes.assertions;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.stream.Stream;

import org.jexten.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import imconfig.Config;
import iti.kukumo.plugin.api.Contributions;
import iti.kukumo.plugin.api.contributions.DataType;
import iti.kukumo.plugin.api.datatypes.Assertion;


@SuppressWarnings({"rawtypes","unchecked"})
class TestDecimalAssertionDataType {

    private final Locale locale = Locale.ENGLISH;
    private final ExtensionManager extensionManager = ExtensionManager.create(ModuleLayerProvider.boot());
    private final Contributions contributions = new Contributions(extensionManager, Config.factory().empty());

    private final DataType<Assertion> floatAssertion = (DataType<Assertion>) contributions.dataTypes()
        .getByName("float-assertion").orElseThrow();
    private final DataType<Assertion> doubleAssertion = (DataType<Assertion>) contributions.dataTypes()
        .getByName("double-assertion").orElseThrow();
    private final DataType<Assertion> decimalAssertion = (DataType<Assertion>) contributions.dataTypes()
        .getByName("decimal-assertion").orElseThrow();


    private static Stream<Arguments> decimalAssertions() {
        return Stream.of(
            arguments("is -7.0", -7.0),
            arguments("is equals to 8.0", 8.0),
            arguments("is not 9.0", 8.0),
            arguments("is not equals to 10.0", 9.0),
            arguments("is greater than 11.0", 12.0),
            arguments("is greater than or equals to 12.0", 12.0),
            arguments("is less than 13.0", 12.0),
            arguments("is less than or equals to 13.0", 13.0)
        );
    }



    @ParameterizedTest
    @MethodSource("decimalAssertions")
    void testFloat(String expression, Double value) {
        Assertion<Float> matcher = floatAssertion.parse(locale, expression);
        assertThat(matcher).as("null assertion for: " + expression).isNotNull();
        assertThat(matcher.test(value.floatValue()))
            .as("failed match for: " +expression + " with " +value).isTrue();
    }


    @ParameterizedTest
    @MethodSource("decimalAssertions")
    void testDouble(String expression, Double value) {
        Assertion<Double> matcher = doubleAssertion.parse(locale, expression);
        assertThat(matcher).as("null assertion for: " + expression).isNotNull();
        assertThat(matcher.test(value))
            .as("failed match for: " +expression + " with " +value).isTrue();
    }


    @ParameterizedTest
    @MethodSource("decimalAssertions")
    void testDecimal(String expression, Double value) {
        Assertion<?> matcher = decimalAssertion.parse(locale, expression);
        assertThat(matcher).as("null assertion for: " + expression).isNotNull();
        assertThat(matcher.test(new BigDecimal(value)))
            .as("failed match for: " +expression + " with " +value).isTrue();
    }

    @Test
    void testFloatNull() {
        floatAssertion.parse(locale, "is null").test(null);
    }


    @Test
    void testFloatNotNull() {
        floatAssertion.parse(locale, "is not null").test(7);
    }

    @Test
    void testDoubleNull() {
        doubleAssertion.parse(locale, "is null").test(null);
    }


    @Test
    void testDoubleNotNull() {
        doubleAssertion.parse(locale, "is not null").test(7);
    }

    @Test
    void testDecimalNull() {
        decimalAssertion.parse(locale, "is null").test(null);
    }


    @Test
    void testDecimalNotNull() {
        decimalAssertion.parse(locale, "is not null").test(7);
    }

}
