package no.nav.tjenestepensjon.simulering.v1.models

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.Pensjonsbeholdningsperiode
import no.nav.tjenestepensjon.simulering.model.domain.pen.*
import no.nav.tjenestepensjon.simulering.v1.models.domain.Inntekt
import no.nav.tjenestepensjon.simulering.v1.models.domain.Simuleringsperiode
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulerPensjonRequestV1
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulertAFPPrivat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

object DtoToV1DomainMapper {

    fun SimulerOffentligTjenestepensjonRequest.toSimulerPensjonRequestV1() = SimulerPensjonRequestV1(
        fnr = FNR(fnr),
        sivilstandkode = this.sivilstandkode.toString(),
        sprak = this.sprak ?: "norsk",
        simuleringsperioder = simuleringsperiodeListe.map { it.toSimuleringsperiode() },
        simulertAFPOffentlig = this.simulertAFPOffentlig?.simulertAFPOffentligBrutto,
        simulertAFPPrivat = this.simulertAFPPrivat?.toSimulertAFPPrivat(),
        pensjonsbeholdningsperioder = this.pensjonsbeholdningsperiodeListe.map { it.toPensjonsbeholdningsperiode() },
        inntekter = this.inntektListe.map { it.toInntekt() }
    )

    private fun SimuleringsperiodeDto.toSimuleringsperiode() =
        Simuleringsperiode(
            datoFom = convertToLocalDate(datoFom),
            utg = this.folketrygdUttaksgrad,
            stillingsprosentOffentlig = this.stillingsprosentOffentlig,
            //Følgende felt er ikke med i v1
//            poengArTom1991 = this.poengArTom1991,
//            poengArFom1992 = this.poengArFom1992,
//            sluttpoengtall = this.sluttpoengtall,
//            anvendtTrygdetid = this.anvendtTrygdetid,
//            forholdstall = this.forholdstall,
//            delingstall = this.delingstall,
//            uforegradVedOmregning = this.uforegradVedOmregning,
//            delytelser = this.delytelser,
        )

    private fun SimulertAFPPrivatDto.toSimulertAFPPrivat() =
        SimulertAFPPrivat(afpOpptjeningTotalbelop, kompensasjonstillegg)

    private fun PensjonsbeholdningsperiodeDto.toPensjonsbeholdningsperiode() = Pensjonsbeholdningsperiode(
        datoFom = LocalDate.from(datoFom.toInstant().atZone(ZoneId.systemDefault())),
        pensjonsbeholdning = this.pensjonsbeholdning.toInt(),
        garantipensjonsbeholdning = this.garantipensjonsbeholdning.toInt(),
        garantitilleggsbeholdning = this.garantitilleggsbeholdning.toInt(),
    )

    private fun InntektDto.toInntekt() = Inntekt(convertToLocalDate(datoFom), inntekt)

    private fun convertToLocalDate(dato: Date): LocalDate =
        LocalDate.from(dato.toInstant().atZone(ZoneId.systemDefault()))
}


