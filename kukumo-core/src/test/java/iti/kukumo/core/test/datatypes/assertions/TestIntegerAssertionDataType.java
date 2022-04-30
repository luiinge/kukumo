/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package iti.kukumo.core.test.datatypes.assertions;




import imconfig.Config;
import iti.kukumo.plugin.api.Contributions;
import iti.kukumo.plugin.api.contributions.DataType;
import iti.kukumo.plugin.api.datatypes.Assertion;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import org.jexten.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;


@SuppressWarnings({"rawtypes","unchecked"})
class TestIntegerAssertionDataType {

    private final Locale locale = Locale.ENGLISH;
    private final ExtensionManager extensionManager = ExtensionManager.create(ModuleLayerProvider.boot());
    private final Contributions contributions = new Contributions(extensionManager, Config.factory().empty());

    private final DataType<Assertion> integerAssertion = (DataType<Assertion>) contributions.dataTypes()
        .getByName("integer-assertion").orElseThrow();
    private final DataType<Assertion> longAssertion = (DataType<Assertion>) contributions.dataTypes()
        .getByName("long-assertion").orElseThrow();


    private static Stream<Arguments> integerAssertions() {
        return Stream.of(
            arguments("is -7", -7),
            arguments("is equals to 8,000", 8000),
            arguments("is greater than 11", 12),
            arguments("is greater than or equals to 12", 12),
            arguments("is less than 13", 12),
            arguments("is less than or equals to 13", 13),
            arguments("is not -7", -8),
            arguments("is not equals to 8,000", 8001),
            arguments("is not greater than 11", 11),
            arguments("is not greater than or equals to 12", 10),
            arguments("is not less than 13", 13),
            arguments("is not less than or equals to 13", 15)
        );
    }



    @ParameterizedTest
    @MethodSource("integerAssertions")
    void testInteger(String expression, Integer value) {
        Assertion<Integer> matcher = integerAssertion.parse(locale, expression);
        assertThat(matcher).as("null assertion for: " + expression).isNotNull();
        assertThat(matcher.test(value))
            .as("failed match for: " +expression + " with " +value).isTrue();
    }


    @ParameterizedTest
    @MethodSource("integerAssertions")
    void testLong(String expression, Integer value) {
        Assertion<Long> matcher = longAssertion.parse(locale, expression);
        assertThat(matcher).as("null assertion for: " + expression).isNotNull();
        assertThat(matcher.test(value.longValue()))
            .as("failed match for: " +expression + " with " +value).isTrue();
    }


    @Test
    void testIntegerNull() {
        integerAssertion.parse(locale, "is null").test(null);
    }


    @Test
    void testIntegerNotNull() {
        integerAssertion.parse(locale, "is not null").test(7);
    }

    @Test
    void testLongNull() {
        longAssertion.parse(locale, "is null").test(null);
    }


    @Test
    void testLongNotNull() {
        longAssertion.parse(locale, "is not null").test(7);
    }




}
