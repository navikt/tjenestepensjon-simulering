package no.nav.tjenestepensjon.simulering.model.domain.pen

//TODO remove this method after testing
data class SimulerAFPOffentligLivsvarigResponseUtvidetForTest(
    var fnr: String,
    var afpYtelser: List <AFPOffentligLivsvarigYtelseUtvidetTest>,
    var tpLeverandor: String?,
    var delingstall: Delingstall?)
