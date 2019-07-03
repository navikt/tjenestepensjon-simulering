package no.nav.tjenestepensjon.simulering.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import static no.nav.tjenestepensjon.simulering.util.Utils.convertToDato;
import static no.nav.tjenestepensjon.simulering.util.Utils.createDate;
import static no.nav.tjenestepensjon.simulering.util.Utils.get4DigitBirthYear;
import static no.nav.tjenestepensjon.simulering.util.Utils.getBirthDate;
import static no.nav.tjenestepensjon.simulering.util.Utils.isSameDay;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;

class UtilsTest {

    @Test
    void shouldConvertTwoToFourDigits() {
        String born1925 = "01012522500";
        assertThat(get4DigitBirthYear(born1925), is(1925));

        String born1825 = "01017552500";
        assertThat(get4DigitBirthYear(born1825), is(1875));

        String born2025 = "01012582500";
        assertThat(get4DigitBirthYear(born2025), is(2025));

        String born1960 = "01016092500";
        assertThat(get4DigitBirthYear(born1960), is(1960));
    }

    @Test
    void shouldGetBirthDate() {
        String born1925 = "01012522500";
        assertThat(isSameDay(getBirthDate(born1925), createDate(1925, Calendar.JANUARY, 1)), is(true));

        String born1825 = "01017552500";
        assertThat(isSameDay(getBirthDate(born1825), createDate(1875, Calendar.JANUARY, 1)), is(true));

        String born2025 = "01012582500";
        assertThat(isSameDay(getBirthDate(born2025), createDate(2025, Calendar.JANUARY, 1)), is(true));

        String born1960 = "01016092500";
        assertThat(isSameDay(getBirthDate(born1960), createDate(2025, Calendar.JANUARY, 1)), is(false));
    }

    @Test
    void shoulDetermineSameDay() {
        assertThat(isSameDay(createDate(1925, Calendar.JANUARY, 1), createDate(1925, Calendar.JANUARY, 1)), is(true));
        assertThat(isSameDay(createDate(1925, Calendar.JANUARY, 1), createDate(1875, Calendar.JANUARY, 1)), is(false));
    }

    @Test
    void shouldConvertStartAlderStartManedToDate() {
        Date date = convertToDato("01016092500", 80, 0, false);
        assertThat(isSameDay(date, createDate(2040, Calendar.JANUARY, 1)), is(true));

        Date noMonthOrYear = convertToDato("01016092500", null, null, false);
        assertThat(noMonthOrYear, is(nullValue()));
    }

    @Test
    void shouldConvertStartAlderSluttManedToTypicalDate() {
        Date date = convertToDato("01016092500", 60, 3, true);
        assertThat(isSameDay(date, createDate(2020, Calendar.APRIL, 30)), is(true));
    }

    @Test
    void shouldConvertStartAlderSluttManedToFebruaryDate() {
        Date date = convertToDato("01016092500", 60, 1, true);
        assertThat(isSameDay(date, createDate(2020, Calendar.FEBRUARY, 29)), is(true));
    }
}