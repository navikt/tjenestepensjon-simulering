package no.nav.tjenestepensjon.simulering.consumer

import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import java.util.concurrent.Callable

class FindTpLeverandorCallable(private val tpOrdning: TPOrdning, private val tpConfigConsumer: TpConfigConsumer, private val tpLeverandorList: List<TpLeverandor>) : Callable<TpLeverandor> {
    @Throws(Exception::class)
    override fun call(): TpLeverandor {
        val tpLeverandor = tpConfigConsumer.findTpLeverandor(tpOrdning)
        return tpLeverandorList.first { l: TpLeverandor -> tpLeverandor.equals(l.name, ignoreCase = true) }
    }

}