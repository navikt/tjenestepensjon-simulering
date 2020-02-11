package no.nav.tjenestepensjon.simulering.model.v1.request

import com.fasterxml.jackson.annotation.JsonCreator
import no.nav.tjenestepensjon.simulering.domain.DelytelseType.*
import no.nav.tjenestepensjon.simulering.model.v1.domain.Simuleringsperiode

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
) {
    constructor(periode: Simuleringsperiode) : this(
            poengArTom1991 = periode.poengArTom1991,
            poengArFom1992 = periode.poengArFom1992,
            sluttpoengtall = periode.sluttpoengtall,
            forholdstall_uttak = periode.forholdstall,
            anvendtTrygdetid = periode.anvendtTrygdetid,
            uforegradVedOmregning = periode.uforegradVedOmregning,
            basisgp = periode.getDelytelseBelop(BASISGP),
            basispt = periode.getDelytelseBelop(BASISPT),
            basistp = periode.getDelytelseBelop(BASISTP),
            skjermingstillegg = periode.getDelytelseBelop(SKJERMINGSTILLEGG)
    )
}