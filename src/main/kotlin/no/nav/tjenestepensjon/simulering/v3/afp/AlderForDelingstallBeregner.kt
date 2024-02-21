package no.nav.tjenestepensjon.simulering.v3.afp

import no.nav.tjenestepensjon.simulering.model.domain.pen.Alder
import no.nav.tjenestepensjon.simulering.model.domain.pen.AlderForDelingstall
import java.time.LocalDate
import java.time.Period

object AlderForDelingstallBeregner {
    private val hoyesteAlderForDelingstall = Alder(70, 0)

    fun bestemAldreForDelingstall(fodselsdato: LocalDate, uttaksdato: LocalDate): List<AlderForDelingstall> {
        val aarGammelVedUttak = uttaksdato.year - fodselsdato.year

        if (aarGammelVedUttak == 62) {
            val noyaktigAlderVedUttak = bestemAlderVedDato(fodselsdato, uttaksdato)

            val arskifteTilBrukerenBlir63 = LocalDate.of(uttaksdato.year + 1, 1, 1)
            val alderVedAarskifteTil63 = bestemAlderVedDato(fodselsdato, arskifteTilBrukerenBlir63)

            return listOf(AlderForDelingstall(noyaktigAlderVedUttak, uttaksdato), AlderForDelingstall(alderVedAarskifteTil63, arskifteTilBrukerenBlir63))
        }
        if (aarGammelVedUttak >= 70) {
            return listOf(AlderForDelingstall(hoyesteAlderForDelingstall, uttaksdato))
        }
        return listOf(AlderForDelingstall(bestemAlderVedDato(fodselsdato, uttaksdato), uttaksdato))
    }

    private fun bestemAlderVedDato(fodselsdato: LocalDate, date: LocalDate): Alder {
        val periode = Period.between(fodselsdato, date)
        return Alder(periode.years, periode.months)
    }
}