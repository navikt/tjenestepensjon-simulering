package no.nav.tjenestepensjon.simulering.v2.consumer

import no.nav.tjenestepensjon.simulering.consumer.TpConfigConsumer
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import java.util.concurrent.Callable

class FindTpLeverandorCallable(
        private val tpOrdning: TPOrdning,
        private val tpConfigConsumer: TpConfigConsumer,
        private val tpLeverandorList: List<TpLeverandor>
) : Callable<TpLeverandor> {

    override fun call(): TpLeverandor {
        val tpLeverandor = tpConfigConsumer.findTpLeverandor(tpOrdning)
        return tpLeverandorList.first { l: TpLeverandor -> tpLeverandor.equals(l.name, ignoreCase = true) }
    }
}