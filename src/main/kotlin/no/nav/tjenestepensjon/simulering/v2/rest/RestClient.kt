package no.nav.tjenestepensjon.simulering.v2.rest

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequestV2
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class RestClient(private val spkPre2025Client: WebClient) {
    private val log = KotlinLogging.logger {}

    fun getResponse(request: SimulerPensjonRequestV2, tpOrdning: TPOrdningIdDto): SimulerOffentligTjenestepensjonResponse = spkPre2025Client
        .post()
        .uri("/nav/pensjon/prognose/v1")
        .bodyValue(request)
        .retrieve()
        .bodyToMono<SimulerOffentligTjenestepensjonResponse>()
        .block()
        ?: SimulerOffentligTjenestepensjonResponse(request.sisteTpnr, tpOrdning.tpId)

    fun ping(): String {
        val request = dummyRequest()
        val url = "/nav/pensjon/prognose/v1"
        log.info { "Pinging SPK at url $url with request $request" }
        return try {
            spkPre2025Client
                .post()
                .uri(url)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String::class.java)
                .block() ?: "No body received"
        } catch (e: WebClientResponseException) {
            val msg = "Failed to ping SPK at url $url, got: ${e.responseBodyAsString} for request $request, ${e.message}"
            log.error(e) { msg }
            msg
        } catch (e: WebClientRequestException) {
            val msg = "Failed to ping SPK at uri ${e.uri} with request $request"
            log.error(e) { msg }
            msg
        }
    }

    private fun dummyRequest() : String =
        """ 
            {
            "sisteTpnr": "3010",
            "fnr": "14866297763",
            "fodselsdato": "1962-06-14",
            "sivilstandkode": "GIFT",
            "sprak": "nb",
            "simuleringsperioder": [{
            "datoFom": "2025-06-01",
            "folketrygdUttaksgrad": 100,
            "stillingsprosentOffentlig": 0,
            "simulerAFPOffentligEtterfulgtAvAlder": "J"
            }],
            "simuleringsdata": [{
            "datoFom": "2025-06-01",
            "poengArTom1991": 11,
            "poengArFom1992": 29,
            "sluttpoengtall": 5.56,
            "anvendtTrygdetid": 40,
            "forholdstall": 1.099,
            "delingstall": 15.73,
            "uforegradVedOmregning": 0,
            "basisgp": 89872.0,
            "basistp": 237768.0,
            "basispt": 0.0
            }],
            "simulertAFPOffentlig": {
            "simulertAFPOffentligBrutto": 348036,
            "tpi": 785180.0
            },
            "simulertAFPPrivat": null,
            "tpForhold": [{
            "tpnr": "3010",
            "opptjeningsperioder": [{
            "stillingsprosent": 76,
            "datoFom": "1987-08-01",
            "datoTom": "1988-07-31",
            "faktiskHovedlonn": 785180,
            "stillingsuavhengigTilleggslonn": 0,
            "aldersgrense": 70,
            "risikofellesskap": null
            },
            {
            "stillingsprosent": 100,
            "datoFom": "1988-08-01",
            "datoTom": "2009-05-02",
            "faktiskHovedlonn": 785180,
            "stillingsuavhengigTilleggslonn": 0,
            "aldersgrense": 70,
            "risikofellesskap": null
            },
            {
            "stillingsprosent": 100,
            "datoFom": "2009-05-03",
            "datoTom": "9999-12-31",
            "faktiskHovedlonn": 785180,
            "stillingsuavhengigTilleggslonn": 0,
            "aldersgrense": 70,
            "risikofellesskap": null
            }
            ]
            }],
            "pensjonsbeholdningsperioder": [{
            "datoFom": "2025-06-01",
            "pensjonsbeholdning": 4409390,
            "garantipensjonsbeholdning": -883500,
            "garantitilleggsbeholdning": -1022307
            }],
            "inntekter": [{
            "datoFom": "2010-10-01",
            "inntekt": 0.0
            }],
            "tpnr": "3010"
            }
        """.trimIndent()
}
