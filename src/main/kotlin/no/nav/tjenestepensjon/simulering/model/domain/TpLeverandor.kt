package no.nav.tjenestepensjon.simulering.model.domain


data class TpLeverandor(val name: String,
                        val impl: EndpointImpl,
                        val simuleringUrl: String,
                        val stillingsprosentUrl: String) {

    enum class EndpointImpl {
        SOAP, REST
    }
}