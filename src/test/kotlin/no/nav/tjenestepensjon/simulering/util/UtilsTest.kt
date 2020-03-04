package no.nav.tjenestepensjon.simulering.util

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class UtilsTest {

    @Test
    fun `Should convert start alder start maned to date`() {
        val date = fnr.datoAtAge(80, 0, false)
        assertEquals(LocalDate.of(2040, 1, 1), date)
    }

    @Test
    fun `Should convert start alder slutt maned to typical date`() {
        val date = fnr.datoAtAge(60, 3, true)
        assertEquals(LocalDate.of(2020, 4, 30), date)
    }

    @Test
    fun `Should convert start alder slutt maned to february date`() {
        val date = fnr.datoAtAge(60, 1, true)
        assertEquals(LocalDate.of(2020, 2, 29), date)
    }

    companion object {
        val fnr = FNR("01016092500")
    }
}