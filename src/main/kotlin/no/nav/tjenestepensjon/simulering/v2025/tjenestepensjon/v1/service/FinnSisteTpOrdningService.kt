package no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service

import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningDto

interface FinnSisteTpOrdningService {

    /*
    *  En enkel versjon av siste ordning som returnerer "spk" om det ligger i listen
    *  eller første i listen. Liste kan ikke være tom
    *
    * Avventer en avansert versjon av FinnSisteOrdning service fra SPK
    *
    * */
    fun finnSisteOrdning(tpOrdninger: List<TpOrdningDto>): String {
        val sisteOrdning = tpOrdninger
            .flatMap { it.alias }
            .any { "spk".equals(it, ignoreCase = true) }
        return if (sisteOrdning) "spk" else tpOrdninger.flatMap { it.alias }.firstOrNull() ?: TP_ORDNING_UTEN_ALIAS
    }

    fun finnSisteOrdningKandidater(tpOrdninger: List<TpOrdningDto>): List<String>

    companion object{
        const val TP_ORDNING_UTEN_ALIAS = "tp-ordning er uten alias i tpregisteret"
    }
}