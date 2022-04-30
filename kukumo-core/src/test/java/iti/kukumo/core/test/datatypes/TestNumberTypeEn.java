/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package iti.kukumo.core.test.datatypes;

import iti.kukumo.core.datatypes.*;
import iti.kukumo.plugin.api.KukumoPluginException;
import java.math.BigDecimal;
import java.util.Locale;
import org.junit.jupiter.api.*;


class TestNumberTypeEn {

    private static final Locale LOCALE = Locale.ENGLISH;


    @Test
    void testAttemptParseWithWrongValue() {
        var type = new IntegerDataType();
        Assertions.assertThrowsExactly(
            KukumoPluginException.class,
            () -> type.parse(LOCALE, "xxxx"),
            "Error parsing type int using language en: 'xxxxx'"
        );
    }


    @Test
    void testInteger() {
        var type = new IntegerDataType();
        Assertions.assertTrue(type.matcher(LOCALE, "12345").matches());
        Assertions.assertEquals(12345, type.parse(LOCALE, "12345"));
        Assertions.assertTrue(type.matcher(LOCALE, "12,345").matches());
        Assertions.assertEquals(12345, type.parse(LOCALE, "12,345"));
        Assertions.assertFalse(type.matcher(LOCALE, "12,345.54").matches());
        Assertions.assertFalse(type.matcher(LOCALE, "xxxxx").matches());
    }


    @Test
    void testDecimal() {
        var type = new DecimalDataType();
        Assertions.assertFalse(type.matcher(LOCALE, "12345").matches());
        Assertions.assertTrue(type.matcher(LOCALE, "12345.0").matches());
        Assertions.assertEquals(BigDecimal.valueOf(12345.0),type.parse(LOCALE, "12345.0"));
        Assertions.assertFalse(type.matcher(LOCALE, "12,345").matches());
        Assertions.assertTrue(type.matcher(LOCALE, "12,345.0").matches());
        Assertions.assertEquals(BigDecimal.valueOf(12345.0), type.parse(LOCALE, "12,345.0"));
        Assertions.assertTrue(type.matcher(LOCALE, "12,345.54").matches());
        Assertions.assertEquals(BigDecimal.valueOf(12345.54),type.parse(LOCALE, "12,345.54"));
        Assertions.assertFalse(type.matcher(LOCALE, "xxxxx").matches());
    }

}
