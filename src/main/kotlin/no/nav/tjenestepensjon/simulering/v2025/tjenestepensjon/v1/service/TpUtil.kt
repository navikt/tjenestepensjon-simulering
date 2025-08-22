package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import no.nav.tjenestepensjon.simulering.common.AlderUtil.bestemUttaksalderVedDato
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Maanedsutbetaling
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.domain.Utbetalingsperiode
import java.time.LocalDate

object TpUtil {
    fun grupperMedDatoFra(utbetalingsliste: List<Utbetalingsperiode>, foedselsdato: LocalDate): List<Maanedsutbetaling> {
        return utbetalingsliste
            .groupBy { it.fom }
            .map { (datoFra, ytelser) ->
                val totalMaanedsBelop = ytelser.sumOf { it.maanedligBelop }
                Maanedsutbetaling(datoFra, bestemUttaksalderVedDato(foedselsdato, datoFra), totalMaanedsBelop)
            }
            .sortedBy { it.fraOgMedDato }
    }

    private val fnrRegex = Regex("\\d{11}")
    fun redact(string: String): String = string.replace(fnrRegex) { "***********" }
}