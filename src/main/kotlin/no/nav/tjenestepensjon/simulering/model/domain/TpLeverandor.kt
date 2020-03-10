package no.nav.tjenestepensjon.simulering.model.domain


data class TpLeverandor(val name: String, val url: String, val impl: EndpointImpl?) {

    enum class EndpointImpl {
        SOAP, REST
    }
}