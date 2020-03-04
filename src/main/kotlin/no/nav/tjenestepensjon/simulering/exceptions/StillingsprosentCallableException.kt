package no.nav.tjenestepensjon.simulering.exceptions

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning

class StillingsprosentCallableException(private val msg: String, cause: Throwable, val tpOrdning: TPOrdning) : Throwable(msg, cause) {

    override fun toString() =
            "StillingsprosentCallableException{message'$msg'tpOrdning='$tpOrdning'}"
}