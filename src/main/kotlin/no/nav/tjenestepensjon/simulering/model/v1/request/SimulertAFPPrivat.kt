package no.nav.tjenestepensjon.simulering.model.v1.request

data class SimulertAFPPrivat(
        val afpOpptjeningTotalbelop: Int,
        val kompensasjonstillegg: Double? = null
)