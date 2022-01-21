package no.nav.tjenestepensjon.simulering

import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import java.time.LocalDate

const val defaultFNRString = "01010101010"
val defaultFNR = FNR(defaultFNRString)

const val defaultTpid = "4321"
const val defaultTssid = "1234"
const val defaultForhold = """[{"ytelser":"[]","ordning":"4321"}]"""

const val defaultForholdUrl = "/api/tjenestepensjon/$defaultFNRString/forhold"
const val defaultLeveradorUrl = "/api/tpconfig/tpleverandoer/$defaultTpid"
const val defaultTssnrUrl = "/api/tpconfig/tssnr/$defaultTpid"


const val defaultFomDateString = "1901-01-01"
const val defaultTomDateString = "1901-01-31"
val defaultDatoFom: LocalDate = LocalDate.parse(defaultFomDateString)
val defaultDatoTom: LocalDate = LocalDate.parse(defaultTomDateString)

val defaultTPOrdning = TPOrdning(defaultTssid, defaultTpid)
