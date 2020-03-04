package no.nav.tjenestepensjon.simulering.v1.models.request

data class SimulertAFPPrivat(
        val afpOpptjeningTotalbelop: Int,
        val kompensasjonstillegg: Double? = null
)