package no.nav.tjenestepensjon.simulering.v2.models

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.Pensjonsbeholdningsperiode
import no.nav.tjenestepensjon.simulering.model.domain.pen.*
import no.nav.tjenestepensjon.simulering.v2.models.DtoToV2DomainMapper.convertToLocalDate
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode
import no.nav.tjenestepensjon.simulering.v2.models.request.*
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

object DtoToV2DomainMapper {

    fun SimulerOffentligTjenestepensjonRequest.toSimulerPensjonRequestV2() = SimulerPensjonRequestV2(
        fnr = FNR(fnr),
        fodselsdato = this.fodselsdato,
        sisteTpnr = this.sisteTpnr!!,
        sprak = this.sprak ?: "norsk",
        simulertAFPOffentlig = this.simulertAFPOffentlig?.let {
            SimulertAFPOffentlig(
                it.simulertAFPOffentligBrutto,
                it.tpi
            )
        },
        simulertAFPPrivat = this.simulertAFPPrivat?.toSimulertAFPPrivat(),
        sivilstandkode = this.sivilstandkode,
        inntektListe = this.inntektListe.map { it.toInntekt() },
        pensjonsbeholdningsperiodeListe = this.pensjonsbeholdningsperiodeListe.map { it.toPensjonsbeholdningsperiode() },
        simuleringsperiodeListe = simuleringsperiodeListe.map { it.toSimuleringsperiode() },
        simuleringsdataListe = this.simuleringsdataListe.map { it.toSimuleringsdata() },
        tpForholdListe = this.tpForholdListe.map { it.toTpForhold() },
    )

    private fun SimuleringsperiodeDto.toSimuleringsperiode() =
        Simuleringsperiode(
            datoFom = datoFom.convertToLocalDate(),
            folketrygdUttaksgrad = this.folketrygdUttaksgrad,
            stillingsprosentOffentlig = this.stillingsprosentOffentlig,
            simulerAFPOffentligEtterfulgtAvAlder = this.simulerAFPOffentligEtterfulgtAvAlder,
        )

    private fun SimulertAFPPrivatDto.toSimulertAFPPrivat() =
        SimulertAFPPrivat(afpOpptjeningTotalbelop, kompensasjonstillegg)

    private fun PensjonsbeholdningsperiodeDto.toPensjonsbeholdningsperiode() = Pensjonsbeholdningsperiode(
        datoFom = LocalDate.from(datoFom.toInstant().atZone(ZoneId.systemDefault())),
        pensjonsbeholdning = this.pensjonsbeholdning.toInt(),
        garantipensjonsbeholdning = this.garantipensjonsbeholdning.toInt(),
        garantitilleggsbeholdning = this.garantitilleggsbeholdning.toInt(),
    )

    private fun InntektDto.toInntekt() = Inntekt(datoFom.convertToLocalDate(), inntekt)

    fun Date.convertToLocalDate(): LocalDate =
        LocalDate.from(this.toInstant().atZone(ZoneId.systemDefault()))

    private fun SimuleringsdataDto.toSimuleringsdata() = Simuleringsdata(
        datoFom = datoFom.convertToLocalDate(),
        andvendtTrygdetid = this.andvendtTrygdetid,
        poengArTom1991 = poengArTom1991,
        poengArFom1992 = poengArFom1992,
        uforegradVedOmregning = uforegradVedOmregning,
        basisgp = this.basisgp,
        basispt = this.basispt,
        basistp = this.basistp,
        delingstallUttak = this.delingstallUttak,
        forholdstallUttak = this.forholdstallUttak,
        sluttpoengtall = this.sluttpoengtall,
    )

    private fun TpForholdDto.toTpForhold() = TpForhold(this.tpnr, this.opptjeningsperiodeListe.map {
        Opptjeningsperiode(
            datoFom = it.datoFom.convertToLocalDate(),
            datoTom = it.datoTom.convertToLocalDate(),
            stillingsprosent = it.stillingsprosent.toDouble(),
            aldersgrense = it.aldersgrense,
            faktiskHovedlonn = it.faktiskHovedlonn,
            stillingsuavhengigTilleggslonn = it.stillingsuavhengigTilleggslonn,
        )
    })
}




