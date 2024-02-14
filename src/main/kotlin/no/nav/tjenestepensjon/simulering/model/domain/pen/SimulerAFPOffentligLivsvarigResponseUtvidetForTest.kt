package no.nav.tjenestepensjon.simulering.model.domain.pen

//TODO remove this method after testing
data class SimulerAFPOffentligLivsvarigResponseUtvidetForTest(
    val fnr: String,
    val afpYtelser: List <AFPOffentligLivsvarigYtelseUtvidetTest>,
    val tpLeverandor: String?,
    val delingstall: Delingstall?)
