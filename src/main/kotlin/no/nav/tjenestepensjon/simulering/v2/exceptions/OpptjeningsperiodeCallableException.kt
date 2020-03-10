package no.nav.tjenestepensjon.simulering.v2.exceptions

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning

class OpptjeningsperiodeCallableException(private val msg: String, cause: Throwable, val tpOrdning: TPOrdning) : Throwable(msg, cause) {

    override fun toString() =
            "OpptjeningsperiodeCallableException{message'$msg'tpOrdning='$tpOrdning'}"
}