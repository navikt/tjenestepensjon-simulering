package no.nav.tjenestepensjon.simulering.v1.models.error

abstract class StelvioFault : Throwable() {
    lateinit var errorMessage: String
    lateinit var errorSource: String
    lateinit var errorType: String
    lateinit var rootCause: String
    lateinit var dateTimeStamp: String
}