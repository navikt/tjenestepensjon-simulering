package no.nav.tjenestepensjon.simulering.v2025.afp.v1

import no.nav.tjenestepensjon.simulering.v2025.afp.v1.AlderForDelingstallBeregner
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class AlderForDelingstallBeregnerTest {

    @Test
    fun `bestem alder ved uttaksalder 62 aar`() {
        val alderListe =
            AlderForDelingstallBeregner.bestemAldreForDelingstall(LocalDate.of(1963, 2, 3), LocalDate.of(2025, 4, 1))
        assertEquals(2, alderListe.size)
        assertEquals(62, alderListe[0].alder.aar)
        assertEquals(1, alderListe[0].alder.maaneder)
        assertEquals(62, alderListe[1].alder.aar)
        assertEquals(10, alderListe[1].alder.maaneder)
    }

    @Test
    fun `bestem alder ved uttaksalder over 70`() {
        val alderListe =
            AlderForDelingstallBeregner.bestemAldreForDelingstall(LocalDate.of(1963, 12, 24), LocalDate.of(2036, 4, 1))
        assertEquals(1, alderListe.size)
        assertEquals(70, alderListe[0].alder.aar)
        assertEquals(0, alderListe[0].alder.maaneder)
    }

    @Test
    fun `bestem alder ved uttaksalder mellom 62 og 70 aar`() {
        val alderListe =
            AlderForDelingstallBeregner.bestemAldreForDelingstall(LocalDate.of(1964, 4, 15), LocalDate.of(2029, 1, 1))
        assertEquals(1, alderListe.size)
        assertEquals(64, alderListe[0].alder.aar)
        assertEquals(8, alderListe[0].alder.maaneder)
    }
}