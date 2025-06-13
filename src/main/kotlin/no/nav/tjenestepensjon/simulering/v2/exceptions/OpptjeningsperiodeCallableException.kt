package no.nav.tjenestepensjon.simulering.v2.exceptions

import no.nav.tjenestepensjon.simulering.model.domain.TpOrdningFullDto

class OpptjeningsperiodeCallableException(private val msg: String, cause: Throwable, val tpOrdning: TpOrdningFullDto) : Throwable(msg, cause) {

    override fun toString() =
            "OpptjeningsperiodeCallableException{message'$msg'tpOrdning='$tpOrdning'}"
}