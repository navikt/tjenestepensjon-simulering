package no.nav.tjenestepensjon.simulering.v2.models.request

data class SimulertAFPPrivat(
        val afpOpptjeningTotalbelop: Int,
        val kompensasjonstillegg: Double
)