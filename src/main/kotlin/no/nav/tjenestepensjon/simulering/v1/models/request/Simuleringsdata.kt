package no.nav.tjenestepensjon.simulering.v1.models.request

import no.nav.tjenestepensjon.simulering.domain.DelytelseType
import no.nav.tjenestepensjon.simulering.domain.DelytelseType.*
import no.nav.tjenestepensjon.simulering.v1.models.domain.Simuleringsperiode

data class Simuleringsdata(
        val poengArTom1991: Int,
        val poengArFom1992: Int,
        val sluttpoengtall: Double,
        val anvendtTrygdetid: Int,
        val basisgp: Double?,
        val basistp: Double?,
        val basispt: Double?,
        val forholdstall_uttak: Double,
        val skjermingstillegg: Double?,
        val uforegradVedOmregning: Int
) {
    constructor(periode: Simuleringsperiode) : this(
            poengArTom1991 = periode.poengArTom1991,
            poengArFom1992 = periode.poengArFom1992,
            sluttpoengtall = periode.sluttpoengtall,
            forholdstall_uttak = periode.forholdstall,
            anvendtTrygdetid = periode.anvendtTrygdetid,
            uforegradVedOmregning = periode.uforegradVedOmregning,
            basisgp = periode.getDelytelseBelop(DelytelseType.basisgp),
            basispt = periode.getDelytelseBelop(DelytelseType.basispt),
            basistp = periode.getDelytelseBelop(DelytelseType.basistp),
            skjermingstillegg = periode.getDelytelseBelop(DelytelseType.skjermingstilleg)
    )
}