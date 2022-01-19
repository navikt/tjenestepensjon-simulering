package no.nav.tjenestepensjon.simulering.v1.consumer

import no.nav.tjenestepensjon.simulering.service.TpService
import no.nav.tjenestepensjon.simulering.exceptions.LeveradoerNotFoundException
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import org.springframework.beans.factory.annotation.Qualifier
import java.util.concurrent.Callable

class FindTpLeverandorCallable(
    private val tpOrdning: TPOrdning,
    private val tpService: TpService,
    @Qualifier("tpLeverandor") private val tpLeverandorList: List<TpLeverandor>
) : Callable<TpLeverandor> {
    @Throws(Exception::class)
    override fun call(): TpLeverandor {
        val tpLeverandor = tpService.findTpLeverandor(tpOrdning)
        return tpLeverandorList.firstOrNull { l: TpLeverandor -> tpLeverandor.equals(l.name, ignoreCase = true) }
            ?: throw LeveradoerNotFoundException("Leveradoer not found for tpOrdning ${tpOrdning.tpId}.")
    }

}
