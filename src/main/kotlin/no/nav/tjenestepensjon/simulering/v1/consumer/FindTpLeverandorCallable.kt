package no.nav.tjenestepensjon.simulering.v1.consumer

import no.nav.tjenestepensjon.simulering.consumer.TpConfigConsumer
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import org.springframework.beans.factory.annotation.Qualifier
import java.util.concurrent.Callable

class FindTpLeverandorCallable(
        private val tpOrdning: TPOrdning,
        private val tpConfigConsumer: TpConfigConsumer,
        @Qualifier("tpLeverandor") private val tpLeverandorList: List<TpLeverandor>
) : Callable<TpLeverandor> {
    @Throws(Exception::class)
    override fun call(): TpLeverandor {
        val tpLeverandor = tpConfigConsumer.findTpLeverandor(tpOrdning)
        return tpLeverandorList.first { l: TpLeverandor -> tpLeverandor.equals(l.name, ignoreCase = true) }
    }

}