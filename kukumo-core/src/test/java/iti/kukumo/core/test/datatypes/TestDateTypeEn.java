/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package iti.kukumo.core.test.datatypes;


import iti.kukumo.core.datatypes.*;
import java.time.*;
import java.util.Locale;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


class TestDateTypeEn {

    private static final Locale LOCALE = Locale.ENGLISH;
    private static final DateDataType DATE_TYPE = new DateDataType();
    private static final TimeDataType TIME_TYPE = new TimeDataType();
    private static final DateTimeDataType DATETIME_TYPE = new DateTimeDataType();


    @Test
    void testLocalizedDate1() {
        assertTrue(DATE_TYPE.matcher(LOCALE, "5/30/18").matches());
        assertEquals(
            LocalDate.of(2018, 5, 30),
            DATE_TYPE.parse(LOCALE, "5/30/18")
        );
    }


    @Test
    void testLocalizedDate2() {
        assertTrue(DATE_TYPE.matcher(LOCALE, "Jan 30, 2018").matches());
        assertEquals(
            LocalDate.of(2018, 1, 30),
            DATE_TYPE.parse(LOCALE, "Jan 30, 2018")
        );
    }


    @Test
    void testLocalizedDate3() {
        assertTrue(DATE_TYPE.matcher(LOCALE, "January 30, 2018").matches());
        assertEquals(
            LocalDate.of(2018, 1, 30),
            DATE_TYPE.parse(LOCALE, "January 30, 2018")
        );
    }


    @Test
    void testLocalizedDate4() {
        assertTrue(DATE_TYPE.matcher(LOCALE, "Tuesday, January 30, 2018").matches());
        assertEquals(
            LocalDate.of(2018, 1, 30),
            DATE_TYPE.parse(LOCALE, "Tuesday, January 30, 2018")
        );
    }


    @Test
    void testLocalizedDate5() {
        assertFalse(DATE_TYPE.matcher(LOCALE, "5999/30/18").matches());
    }


    @Test
    void testLocalizedTime1() {
        assertTrue(TIME_TYPE.matcher(LOCALE, "5:35 PM").matches());
        assertEquals(
            LocalTime.of(17, 35),
            TIME_TYPE.parse(LOCALE, "5:35 PM")
        );
    }


    @Test
    void testLocalizedTime2() {
        assertTrue(TIME_TYPE.matcher(LOCALE, "11:35 PM").matches());
        assertEquals(
            LocalTime.of(23, 35),
            TIME_TYPE.parse(LOCALE, "11:35 PM")
        );
    }


    @Test
    void testLocalizedTime3() {
        assertFalse(TIME_TYPE.matcher(LOCALE, "555:66").matches());
    }


    @Test
    void testLocalizedDateTime1() {
        assertTrue(DATETIME_TYPE.matcher(LOCALE, "5/30/18, 5:35 PM").matches());
        assertEquals(
            LocalDateTime.of(2018, 5, 30, 17, 35),
            DATETIME_TYPE.parse(LOCALE, "5/30/18, 5:35 PM")
        );
    }


    @Test
    void testLocalizedDateTime2() {
        assertTrue(DATETIME_TYPE.matcher(LOCALE, "Jan 30, 2018, 5:35 PM").matches());
        assertEquals(
            LocalDateTime.of(2018, 1, 30, 17, 35),
            DATETIME_TYPE.parse(LOCALE, "Jan 30, 2018, 5:35 PM")
        );
    }


    @Test
    void testLocalizedDateTime3() {
        assertTrue(DATETIME_TYPE.matcher(LOCALE, "January 30, 2018, 5:35 PM").matches());
        assertEquals(
            LocalDateTime.of(2018, 1, 30, 17, 35),
            DATETIME_TYPE.parse(LOCALE, "January 30, 2018, 5:35 PM")
        );
    }


    @Test
    void testLocalizedDateTime4() {
        assertTrue(DATETIME_TYPE.matcher(LOCALE, "January 30, 2018, 5:35 PM").matches());
        assertEquals(
            LocalDateTime.of(2018, 1, 30, 17, 35),
            DATETIME_TYPE.parse(LOCALE, "Tuesday, January 30, 2018, 5:35 PM")
        );
    }


    @Test
    void testLocalizedDateTime5() {
        assertFalse(DATETIME_TYPE.matcher(LOCALE, "5999/30/18 555:66").matches());
    }

}
