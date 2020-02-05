package no.nav.tjenestepensjon.simulering.util

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjonimport
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.util.Utils.convertToDato
import no.nav.tjenestepensjon.simulering.util.Utils.createDate
import no.nav.tjenestepensjon.simulering.util.Utils.get4DigitBirthYear
import no.nav.tjenestepensjon.simulering.util.Utils.getBirthDate
import no.nav.tjenestepensjon.simulering.util.Utils.isSameDay
import org.hamcrest.MatcherAssert

org.hamcrest.Matchersimport org.junit.jupiter.api.Testimport java.util.*

internal class UtilsTest {
    @Test
    fun shouldConvertTwoToFourDigits() {
        val born1925 = "01012522500"
        assertThat(get4DigitBirthYear(born1925), Matchers.`is`(1925))
        val born1825 = "01017552500"
        assertThat(get4DigitBirthYear(born1825), Matchers.`is`(1875))
        val born2025 = "01012582500"
        assertThat(get4DigitBirthYear(born2025), Matchers.`is`(2025))
        val born1960 = "01016092500"
        assertThat(get4DigitBirthYear(born1960), Matchers.`is`(1960))
    }

    @Test
    fun shouldGetBirthDate() {
        val born1925 = "01012522500"
        assertThat(isSameDay(getBirthDate(born1925), createDate(1925, Calendar.JANUARY, 1)), Matchers.`is`(true))
        val born1825 = "01017552500"
        assertThat(isSameDay(getBirthDate(born1825), createDate(1875, Calendar.JANUARY, 1)), Matchers.`is`(true))
        val born2025 = "01012582500"
        assertThat(isSameDay(getBirthDate(born2025), createDate(2025, Calendar.JANUARY, 1)), Matchers.`is`(true))
        val born1960 = "01016092500"
        assertThat(isSameDay(getBirthDate(born1960), createDate(2025, Calendar.JANUARY, 1)), Matchers.`is`(false))
    }

    @Test
    fun shoulDetermineSameDay() {
        assertThat(isSameDay(createDate(1925, Calendar.JANUARY, 1), createDate(1925, Calendar.JANUARY, 1)), Matchers.`is`(true))
        assertThat(isSameDay(createDate(1925, Calendar.JANUARY, 1), createDate(1875, Calendar.JANUARY, 1)), Matchers.`is`(false))
    }

    @Test
    fun shouldConvertStartAlderStartManedToDate() {
        val date: Date = convertToDato("01016092500", 80, 0, false)
        assertThat(isSameDay(date, createDate(2040, Calendar.JANUARY, 1)), Matchers.`is`(true))
        val noMonthOrYear: Date = convertToDato("01016092500", null, null, false)
        MatcherAssert.assertThat<Date>(noMonthOrYear, Matchers.`is`(Matchers.nullValue()))
    }

    @Test
    fun shouldConvertStartAlderSluttManedToTypicalDate() {
        val date: Date = convertToDato("01016092500", 60, 3, true)
        assertThat(isSameDay(date, createDate(2020, Calendar.APRIL, 30)), Matchers.`is`(true))
    }

    @Test
    fun shouldConvertStartAlderSluttManedToFebruaryDate() {
        val date: Date = convertToDato("01016092500", 60, 1, true)
        assertThat(isSameDay(date, createDate(2020, Calendar.FEBRUARY, 29)), Matchers.`is`(true))
    }
}