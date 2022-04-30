/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package iti.kukumo.core.test.datatypes;


import iti.kukumo.core.datatypes.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


class TestDateTypeISO {

    private static final DateDataType DATE_TYPE = new DateDataType();
    private static final TimeDataType TIME_TYPE = new TimeDataType();
    private static final DateTimeDataType DATETIME_TYPE = new DateTimeDataType();

    private static final List<Locale> testLocales = List.of(
        Locale.CANADA, Locale.CHINESE, Locale.ENGLISH, Locale.JAPANESE, Locale.FRENCH, Locale.GERMAN
    );

    @Test
    void testISODate() {
        // ISO date should be accepted by any locale
        for (Locale locale : testLocales) {
            assertTrue(DATE_TYPE.matcher(locale, "2018-05-30").matches());
            assertEquals(DATE_TYPE.parse(locale, "2018-05-30"),LocalDate.of(2018, 5, 30));
        }
    }


    @Test
    void testISOTime() {
        // ISO time should be accepted by any locale
        for (Locale locale : testLocales) {
            assertTrue(TIME_TYPE.matcher(locale, "17:35").matches());
            assertEquals(TIME_TYPE.parse(locale, "17:35"),LocalTime.of(17, 35));
            assertTrue(TIME_TYPE.matcher(locale, "17:35:29").matches());
            assertEquals(TIME_TYPE.parse(locale, "17:35:29"),LocalTime.of(17, 35, 29));
            assertTrue(TIME_TYPE.matcher(locale, "17:35:29.743").matches());
            assertEquals(TIME_TYPE.parse(locale, "17:35:29.743"),LocalTime.of(17, 35, 29, 743000000));
        }
    }


    @Test
    void testISODateTime() {
        // ISO time should be accepted by any locale
        for (Locale locale : testLocales) {
            assertTrue(DATETIME_TYPE.matcher(locale, "2018-05-30T17:35").matches());
            assertEquals(DATETIME_TYPE.parse(locale, "2018-05-30T17:35"),LocalDateTime.of(2018, 5, 30, 17, 35));
            assertTrue(DATETIME_TYPE.matcher(locale, "2018-05-30T17:35:29").matches());
            assertEquals(DATETIME_TYPE.parse(locale, "2018-05-30T17:35:29"),LocalDateTime.of(2018, 5, 30, 17, 35, 29));
            assertTrue(DATETIME_TYPE.matcher(locale, "2018-05-30T17:35:29.743").matches());
            assertEquals(DATETIME_TYPE.parse(locale, "2018-05-30T17:35:29.743"),LocalDateTime.of(2018, 5, 30, 17, 35, 29, 743000000));
        }
    }

}
