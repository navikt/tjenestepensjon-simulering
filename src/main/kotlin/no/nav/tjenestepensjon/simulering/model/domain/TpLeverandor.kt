package no.nav.tjenestepensjon.simulering.model.domain


data class TpLeverandor(val name: String, val url: String, val impl: EndpointImpl?, val maskinportenIntegrasjon: Boolean? = false) {

    enum class EndpointImpl {
        SOAP, REST
    }
}