package no.nav.tjenestepensjon.simulering.v1.domain


data class TpLeverandor(val name: String, val url: String, val impl: EndpointImpl) {

    enum class EndpointImpl {
        SOAP, REST
    }
}