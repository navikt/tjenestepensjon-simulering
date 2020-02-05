package no.nav.tjenestepensjon.simulering.model.v1.request

import com.fasterxml.jackson.annotation.JsonCreator

data class SimulertAFPPrivat @JsonCreator constructor(
        val afpOpptjeningTotalbelop: Int,
        val kompensasjonstillegg: Double? = null
)