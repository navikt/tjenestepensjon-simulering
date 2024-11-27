package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.dto

enum class SPKYtelse {
    PAASLAG, APOF2020, OT6370, SAERALDERSPAASLAG, OAFP, BTP;

    companion object {
        fun hentAlleUnntattType(vararg typer: SPKYtelse): List<String> {
            return entries.filter { it !in typer }.map { it.name }
        }
    }
}