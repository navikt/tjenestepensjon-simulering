package no.nav.tjenestepensjon.simulering.model.domain.pen

data class HentDelingstallRequest(val arskull: Int, val alder: List<Alder>)
