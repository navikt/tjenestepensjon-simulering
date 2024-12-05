package no.nav.tjenestepensjon.simulering.sporingslogg

data class SporingDto(
    val person: String,
    val mottaker: String,
    val tema: String,
    val behandlingsGrunnlag: String,
    val uthentingsTidspunkt: String,
    var dataForespoersel: String,
    var leverteData: String = "(no data)"
)
