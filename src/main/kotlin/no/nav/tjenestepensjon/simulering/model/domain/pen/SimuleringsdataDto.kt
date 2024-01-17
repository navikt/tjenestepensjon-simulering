package no.nav.tjenestepensjon.simulering.model.domain.pen

import java.util.*

data class SimuleringsdataDto (
    val datoFom: Date,
    val andvendtTrygdetid: Int,
    val poengArTom1991: Int,
    val poengArFom1992: Int,
    val uforegradVedOmregning: Int,
    val basisgp: Double,
    val basispt: Double,
    val basistp: Double,
    val delingstallUttak: Double,
    val forholdstallUttak: Double,
    val sluttpoengtall: Double,
)
