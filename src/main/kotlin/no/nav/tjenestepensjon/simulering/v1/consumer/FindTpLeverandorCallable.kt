package no.nav.tjenestepensjon.simulering.v1.consumer

import no.nav.tjenestepensjon.simulering.exceptions.LeveradoerNotFoundException
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.service.TpClient
import org.springframework.beans.factory.annotation.Qualifier
import java.util.concurrent.Callable

class FindTpLeverandorCallable(
    private val tpOrdning: TPOrdning,
    private val tpClient: TpClient,
    @Qualifier("tpLeverandor") private val tpLeverandorList: List<TpLeverandor>
) : Callable<TpLeverandor> {
    @Throws(Exception::class)
    override fun call(): TpLeverandor {
        return tpClient.findTpLeverandor(tpOrdning).let { tpLeverandor ->
            tpLeverandorList.firstOrNull { l: TpLeverandor -> tpLeverandor.equals(l.name, ignoreCase = true) }
        } ?: throw LeveradoerNotFoundException("Leveradoer not found for tpOrdning ${tpOrdning.tpId}.")

    }

}
