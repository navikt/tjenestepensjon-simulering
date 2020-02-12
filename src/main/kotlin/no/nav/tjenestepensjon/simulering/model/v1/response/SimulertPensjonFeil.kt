package no.nav.tjenestepensjon.simulering.model.v1.response

data class SimulertPensjonFeil(
        override var status: String? = null,
        val feilkode: String? = null,
        val feilbeskrivelse: String? = null
): SimulertPensjon()