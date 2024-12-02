package no.nav.tjenestepensjon.simulering.v2.rest

import no.nav.tjenestepensjon.simulering.model.domain.Pensjonsbeholdningsperiode
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.v2.models.domain.SivilstandCodeEnum
import no.nav.tjenestepensjon.simulering.v2.models.request.*
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.LocalDate

@Service
class RestClient(private val spkPre2025Client: WebClient) {

    fun getResponse(request: SimulerPensjonRequestV2, tpOrdning: TPOrdningIdDto): SimulerOffentligTjenestepensjonResponse = spkPre2025Client
        .post()
        .uri("/nav/pensjon/prognose/v1")
        .bodyValue(request)
        .retrieve()
        .bodyToMono<SimulerOffentligTjenestepensjonResponse>()
        .block()
        ?: SimulerOffentligTjenestepensjonResponse(request.sisteTpnr, tpOrdning.tpId)

    fun ping(): String = spkPre2025Client
        .post()
        .uri("/nav/pensjon/prognose/v1")
        .bodyValue(dummyRequest())
        .retrieve()
        .bodyToMono(String::class.java)
        .block() ?: "No body received"

    private fun dummyRequest(fnr: String = "14866297763"): DummyRequestV2 {
        val now = LocalDate.now()
        return DummyRequestV2(
            fnr = Foedselsnummer(fnr),
            fodselsdato = "14-06-1962",
            sisteTpnr = "3010",
            sivilstandkode = SivilstandCodeEnum.UGIF,
            inntektListe = listOf(
                Inntekt(
                    datoFom = LocalDate.of(now.year - 2, 1, 1),
                    inntekt = 900000.0
                )
            ),
            simuleringsperiodeListe = listOf(
                Simuleringsperiode(
                    datoFom = LocalDate.of(now.year + 1, 1, 1),
                    folketrygdUttaksgrad = 20,
                    stillingsprosentOffentlig = 100,
                    simulerAFPOffentligEtterfulgtAvAlder = false
                )
            ),
            simuleringsdataListe = listOf(
                Simuleringsdata(
                    datoFom = LocalDate.of(now.year + 1, 1, 1),
                    andvendtTrygdetid = 40,
                    poengArTom1991 = 30,
                    poengArFom1992 = 40,
                    uforegradVedOmregning = null,
                    basisgp = 96883.0,
                    basispt = 97829.58,
                    basistp = 0.0,
                    delingstallUttak = 16.81,
                    forholdstallUttak = 1.184,
                    sluttpoengtall = 5.28
                )
            )
        )
    }


    data class DummyRequestV2(
        var fnr: Foedselsnummer,
        var fodselsdato: String,
        var sisteTpnr: String,
        var sprak: String? = null,
        var simulertAFPOffentlig: SimulertAFPOffentlig? = null,
        var simulertAFPPrivat: SimulertAFPPrivat? = null,
        var sivilstandkode: SivilstandCodeEnum,
        var inntektListe: List<Inntekt>,
        var pensjonsbeholdningsperiodeListe: List<Pensjonsbeholdningsperiode> = emptyList(),
        var simuleringsperiodeListe: List<Simuleringsperiode>,
        var simuleringsdataListe: List<Simuleringsdata>,
        var tpForholdListe: List<TpForhold> = emptyList()
    )

    data class Foedselsnummer(val fnr: String)
}
