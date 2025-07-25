package no.nav.tjenestepensjon.simulering

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningFullDto
import java.time.LocalDate

const val defaultFNRString = "01010101010"
val defaultFNR = FNR(defaultFNRString)
const val fnrUtenMedlemskap = "01027701010"
const val fnrMedEttMedlemskapITPOrdning = "01037701010"
const val defaultTpOrdningNavn = "Test Ordning"
const val defaultDatoSistOpptjening = "2023-01-01"

const val defaultTpid = "4321"
const val defaultTssid = "1234"
const val defaultForhold = """{"fnr":"$defaultFNRString","forhold":[{"tpNr":"$defaultTpid","tpOrdningNavn":"$defaultTpOrdningNavn","datoSistOpptjening":"$defaultDatoSistOpptjening"}]}"""

const val defaultTjenestepensjonUrl = "/api/intern/tjenestepensjon/forhold/"
const val defaultLeveradorUrl = "/api/tpconfig/tpleverandoer/$defaultTpid"
const val defaultTssnrUrl = "/api/tpconfig/tssnr/$defaultTpid"
val defaultTjenestepensjonRequest: MappingBuilder = get(urlPathEqualTo(defaultTjenestepensjonUrl))
    .withHeader("fnr", equalTo(defaultFNRString))

const val defaultFomDateString = "1901-01-01"
const val defaultTomDateString = "1901-01-31"
val defaultDatoFom: LocalDate = LocalDate.parse(defaultFomDateString)
val defaultDatoTom: LocalDate = LocalDate.parse(defaultTomDateString)

val defaultTPOrdningIdDto = TPOrdningIdDto(defaultTssid, defaultTpid)
val defaultTPOrdningFullDto = TpOrdningFullDto(tssId = defaultTssid, tpNr = defaultTpid, navn = defaultTpOrdningNavn)
