package no.nav.tjenestepensjon.simulering.sporingslogg

enum class Organisasjon(val organisasjonsnummer: String, fulltNavn: String, alias: String) {
    SPK("982583462", "Statens Pensjonskasse", "spk"),
    KLP("938708606", "Kommunal Landspensjonskasse", "klp"),
    NAV("889640782", "Nav", "nav"),
    STB("931936492", "Storebrand", "stb"),
}