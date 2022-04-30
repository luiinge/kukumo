/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package iti.kukumo.core.test.datatypes.assertions;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

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
class TestStringAssertionDataType {

    private final Locale locale = Locale.ENGLISH;
    private final ExtensionManager extensionManager = ExtensionManager.create(ModuleLayerProvider.boot());
    private final Contributions contributions = new Contributions(extensionManager, Config.factory().empty());

    private final DataType<Assertion> stringAssertion = (DataType<Assertion>) contributions.dataTypes()
        .getByName("string-assertion").orElseThrow();



    private static Stream<Arguments> stringAssertions() {
        return Stream.of(
            arguments("is 'text'", "text"),
            arguments("is equals to 'text'", "text"),
            arguments("is 'text' (ignoring case)", "tExt"),
            arguments("is 'text' (ignoring whitespace)", "  text "),
            arguments("starts with 'ab'", "abc"),
            arguments("starts with 'ab' (ignoring case)", "aBc"),
            arguments("ends with 'yz'", "xyz"),
            arguments("ends with 'yz' (ignoring case)", "xYz"),
            arguments("contains 'de'", "cdef"),
            arguments("contains 'de' (ignoring case)","cDef"),

            arguments("is not 'text'", "toxt"),
            arguments("is not equals to 'text'", "tExt"),
            arguments("is not 'text' (ignoring case)", "tOxt"),
            arguments("is not 'text' (ignoring whitespace)", "  toxt "),
            arguments("does not start with 'ab'", "Abc"),
            arguments("does not start with 'ab' (ignoring case)", "qWc"),
            arguments("does not end with 'yz'", "xYz"),
            arguments("does not end with 'yz' (ignoring case)", "xwq"),
            arguments("does not contain 'de'", "cDef"),
            arguments("does not contain 'de' (ignoring case)","cwqf")
        );
    }



    @ParameterizedTest
    @MethodSource("stringAssertions")
    void testString(String expression, String value) {
        Assertion<String> matcher = stringAssertion.parse(locale, expression);
        assertThat(matcher).as("null assertion for: " + expression).isNotNull();
        assertThat(matcher.test(value))
            .as("failed match for: " +expression + " with " +value).isTrue();
    }


    @Test
    void testStringNull() {
        stringAssertion.parse(locale, "is null").test(null);
    }

    @Test
    void testStringEmpty() {
        stringAssertion.parse(locale, "is empty").test(null);
    }

    @Test
    void testStringNotNull() {
        stringAssertion.parse(locale, "is not null").test(7);
    }

    @Test
    void testStringNotNullEmpty() {
        stringAssertion.parse(locale, "is not null or empty").test(7);
    }




}
