package no.nav.tjenestepensjon.simulering.model.v1.request

import com.fasterxml.jackson.annotation.JsonCreator

data class Simuleringsdata @JsonCreator constructor(
    var poengArTom1991: Int,
    var poengArFom1992: Int,
    var sluttpoengtall: Double,
    var anvendtTrygdetid: Int,
    var basisgp: Double?,
    var basistp: Double?,
    var basispt: Double?,
    var forholdstall_uttak: Double,
    var skjermingstillegg: Double?,
    var uforegradVedOmregning: Int
)