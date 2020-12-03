package no.nav.tjenestepensjon.simulering.v2.models.request

import no.nav.tjenestepensjon.simulering.domain.Dateable
import java.time.LocalDate

data class Simuleringsdata(
        override var datoFom: LocalDate,
        val andvendtTrygdetid: Int,
        val poengArTom1991: Int,
        val poengArFom1992: Int,
        val uforegradVedOmregning: Int?,
        val basisgp: Double? ,
        val basispt: Double?,
        val basistp: Double?,
        val delingstallUttak: Double,
        val forholdstallUttak: Double,
        val sluttpoengtall: Double?
) : Dateable