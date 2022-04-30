/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package iti.kukumo.core.test.datatypes;


import iti.kukumo.core.datatypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.*;
import java.util.Locale;

import org.junit.jupiter.api.Test;


class TestDateTypeEs {

    private static final Locale LOCALE = Locale.forLanguageTag("es");
    private static final DateDataType DATE_TYPE = new DateDataType();
    private static final TimeDataType TIME_TYPE = new TimeDataType();
    private static final DateTimeDataType DATETIME_TYPE = new DateTimeDataType();


    @Test
    void testLocalizedDate1() {
        assertTrue(DATE_TYPE.matcher(LOCALE, "30/05/18").matches());
        assertEquals(
            LocalDate.of(2018, 5, 30),
            DATE_TYPE.parse(LOCALE, "30/05/18")
        );
    }


    @Test
    void testLocalizedDate2() {
        assertTrue(DATE_TYPE.matcher(LOCALE, "30 de mayo de 2018").matches());
        assertEquals(
            LocalDate.of(2018, 5, 30),
            DATE_TYPE.parse(LOCALE, "30 de mayo de 2018")
        );
    }


    @Test
    void testLocalizedDate3() {
        assertTrue(DATE_TYPE.matcher(LOCALE, "Miércoles, 30 de mayo de 2018").matches());
        assertEquals(
            LocalDate.of(2018, 5, 30),
            DATE_TYPE.parse(LOCALE, "Miércoles, 30 de mayo de 2018")
        );
    }


    @Test
    void testLocalizedDate4() {
        assertFalse(DATE_TYPE.matcher(LOCALE, "5630/18").matches());
    }


    @Test
    void testLocalizedTime1() {
        assertTrue(TIME_TYPE.matcher(LOCALE, "17:35").matches());
        assertEquals(
            LocalTime.of(17, 35),
            TIME_TYPE.parse(LOCALE, "17:35")
        );
    }


    @Test
    void testLocalizedTime2() {
        assertTrue(TIME_TYPE.matcher(LOCALE, "5:35").matches());
        assertEquals(
            LocalTime.of(5, 35),
            TIME_TYPE.parse(LOCALE, "5:35")
        );
    }


    @Test
    void testLocalizedTime3() {
        assertFalse(TIME_TYPE.matcher(LOCALE, "555:66").matches());
    }


    @Test
    void testLocalizedDateTime1() {
        assertTrue(DATETIME_TYPE.matcher(LOCALE, "30/05/18 17:35").matches());
        assertEquals(
            LocalDateTime.of(2018, 5, 30, 17, 35),
            DATETIME_TYPE.parse(LOCALE, "30/05/18 17:35")
        );
    }


    @Test
    void testLocalizedDateTime2() {
        assertTrue(DATETIME_TYPE.matcher(LOCALE, "30 de Mayo de 2018 17:35").matches());
        assertEquals(
            LocalDateTime.of(2018, 5, 30, 17, 35),
            DATETIME_TYPE.parse(LOCALE, "30 de Mayo de 2018 17:35")
        );
    }


}
