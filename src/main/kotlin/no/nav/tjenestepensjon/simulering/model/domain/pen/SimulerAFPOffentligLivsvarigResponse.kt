package no.nav.tjenestepensjon.simulering.model.domain.pen

data class SimulerAFPOffentligLivsvarigResponse(val fnr: String, val afpYtelser: List <AfpOffentligLivsvarigYtelseMedDelingstall>, val tpLeverandor: String?)
