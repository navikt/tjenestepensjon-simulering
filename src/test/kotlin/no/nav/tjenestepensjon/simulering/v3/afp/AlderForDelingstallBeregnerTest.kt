package no.nav.tjenestepensjon.simulering.v3.afp

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class AlderForDelingstallBeregnerTest {

    @Test
    fun bestemAlderVeduttaksalder62Aar() {
        val alderListe = AlderForDelingstallBeregner.bestemAldreForDelingstall(LocalDate.of(1963, 2, 3), LocalDate.of(2025, 4, 1))
        assertEquals(2, alderListe.size)
        assertEquals(62, alderListe[0].alder.aar)
        assertEquals(1, alderListe[0].alder.maaneder)
        assertEquals(62, alderListe[1].alder.aar)
        assertEquals(10, alderListe[1].alder.maaneder)
    }

    @Test
    fun bestemAlderVedUttaksAlderOver70() {
        val alderListe = AlderForDelingstallBeregner.bestemAldreForDelingstall(LocalDate.of(1963, 12, 24), LocalDate.of(2036, 4, 1))
        assertEquals(1, alderListe.size)
        assertEquals(70, alderListe[0].alder.aar)
        assertEquals(0, alderListe[0].alder.maaneder)
    }

    @Test
    fun bestemAlderVedUttaksalderMellom62Og70() {
        val alderListe = AlderForDelingstallBeregner.bestemAldreForDelingstall(LocalDate.of(1964, 4, 15), LocalDate.of(2029, 1, 1))
        assertEquals(1, alderListe.size)
        assertEquals(64, alderListe[0].alder.aar)
        assertEquals(8, alderListe[0].alder.maaneder)
    }
}