package no.nav.tjenestepensjon.simulering.mapper

import no.nav.tjenestepensjon.simulering.domain.DelytelseType
import no.nav.tjenestepensjon.simulering.domain.DelytelseType.*
import no.nav.tjenestepensjon.simulering.model.v1.domain.Delytelse
import no.nav.tjenestepensjon.simulering.model.v1.domain.Simuleringsperiode
import no.nav.tjenestepensjon.simulering.model.v1.request.Simuleringsdata

object SimuleringsdataMapper {
    fun mapToSimuleringsdata(periode: Simuleringsperiode) = with(periode) { Simuleringsdata(
            poengArTom1991 = poengArTom1991,
            poengArFom1992 = poengArFom1992,
            sluttpoengtall = sluttpoengtall,
            forholdstall_uttak = forholdstall,
            anvendtTrygdetid = anvendtTrygdetid,
            uforegradVedOmregning = uforegradVedOmregning,
            basisgp = getDelytelseBelop(delytelser, BASISGP),
            basispt = getDelytelseBelop(delytelser, BASISPT),
            basistp = getDelytelseBelop(delytelser, BASISTP),
            skjermingstillegg = getDelytelseBelop(delytelser, SKJERMINGSTILLEGG)
    ) }

    private fun getDelytelseBelop(delytelser: List<Delytelse>, delytelseType: DelytelseType) =
            delytelser.firstOrNull { delytelse: Delytelse -> delytelse.pensjonstype == delytelseType }?.belop
}