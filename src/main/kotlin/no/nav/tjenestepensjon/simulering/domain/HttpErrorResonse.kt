package no.nav.tjenestepensjon.simulering.domain

// response for 300 and 400 error series
class HttpErrorResonse (
        // Timestamp
        val timestamp: String,
        // ErrorCode, for front-end. Keyword for json language file
        val errorCode: String,
        // Message for exception
        val message: String,
        // Client correlationId
        val clientCorrelationId: String?,
        // Nav correlationId
        val serverCorrelationId: String
)