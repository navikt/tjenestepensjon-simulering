package no.nav.tjenestepensjon.simulering.model.v1.error

abstract class StelvioFault : Throwable() {
    lateinit var errorMessage: String
    lateinit var errorSource: String
    lateinit var errorType: String
    lateinit var rootCause: String
    lateinit var dateTimeStamp: String
}