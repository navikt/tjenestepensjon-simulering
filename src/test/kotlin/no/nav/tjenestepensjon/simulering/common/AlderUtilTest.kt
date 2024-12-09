package no.nav.tjenestepensjon.simulering.common

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class AlderUtilTest {

    @Test
    fun `spesialhaandtering for uttaksalder for brukere foedt foerste i maaneden`() {

        val foedselsdato = LocalDate.of(2001, 1, 1)
        val uttaksdato = LocalDate.of(2063, 2, 1)
        val alder = AlderUtil.bestemUttaksalderVedDato(foedselsdato, uttaksdato)
        assertEquals(62, alder.aar)
        assertEquals(0, alder.maaneder)
    }

    @Test
    fun `bestem uttaksalder for en foedselsdato paa foerste desember`() {

        val foedselsdato = LocalDate.of(2001, 12, 1)
        val uttaksdato = LocalDate.of(2064, 1, 1)
        val alder = AlderUtil.bestemUttaksalderVedDato(foedselsdato, uttaksdato)
        assertEquals(62, alder.aar)
        assertEquals(0, alder.maaneder)
    }

    @Test
    fun `test bestem uttaksalder`() {

        val foedselsdato = LocalDate.of(2000, 3, 15)
        val uttaksdato = LocalDate.of(2062, 4, 1)
        val alder = AlderUtil.bestemUttaksalderVedDato(foedselsdato, uttaksdato)
        assertEquals(62, alder.aar)
        assertEquals(0, alder.maaneder)
    }

    @Test
    fun `test bestem uttaksalder for X aar og 11 maaneder`(){
        val foedselsdato = LocalDate.of(2000, 3, 15)
        val uttaksdato = LocalDate.of(2062, 3, 1)
        val alder = AlderUtil.bestemUttaksalderVedDato(foedselsdato, uttaksdato)
        assertEquals(61, alder.aar)
        assertEquals(11, alder.maaneder)
    }
}