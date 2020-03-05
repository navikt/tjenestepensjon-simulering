package no.nav.tjenestepensjon.simulering.v2.models.domain

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import java.time.LocalDate

class FNRTest {

    @Test
    fun `Should get birth date`() {
        assertEquals(LocalDate.of(1825, 1, 1), FNR("01012552500").birthDate)
        assertEquals(LocalDate.of(1925, 1, 1), FNR("01012522500").birthDate)
        assertEquals(LocalDate.of(2025, 1, 1), FNR("01012582500").birthDate)
        assertNotEquals(LocalDate.of(2025, 1, 1), FNR("01016092500").birthDate)
    }
}