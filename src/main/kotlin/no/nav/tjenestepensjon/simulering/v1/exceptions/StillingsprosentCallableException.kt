package no.nav.tjenestepensjon.simulering.v1.exceptions

import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto

class StillingsprosentCallableException(private val msg: String, cause: Throwable, val tpOrdning: TPOrdningIdDto) : Throwable(msg, cause) {

    override fun toString() =
            "StillingsprosentCallableException{message'$msg'tpOrdning='$tpOrdning'}"
}