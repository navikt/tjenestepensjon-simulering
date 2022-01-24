package no.nav.tjenestepensjon.simulering.service

import no.nav.tjenestepensjon.simulering.config.CacheConfig.Companion.TP_ORDNING_LEVERANDOR_CACHE
import no.nav.tjenestepensjon.simulering.config.CacheConfig.Companion.TP_ORDNING_PERSON_CACHE
import no.nav.tjenestepensjon.simulering.config.CacheConfig.Companion.TP_ORDNING_TSSID_CACHE
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.Forhold
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class TpClient(
    private val webClient: WebClient,
    private val aadClient: AADClient,
    @Value("\${TP_URL}") private var tpUrl: String,
    @Value("\${TP_SCOPE}") private val tpScope: String
) {

    fun getTpOrdningerForPerson(fnr: FNR) = findForhold(fnr).map {
        TPOrdning(tpId = it.ordning, tssId = findTssId(it.ordning))
    }

    @Cacheable(TP_ORDNING_PERSON_CACHE)
    fun findForhold(fnr: FNR) = webClient.get().uri("$tpUrl/api/tjenestepensjon/$fnr/forhold").headers {
        it.setBearerAuth(aadClient.getToken(tpScope))
    }.retrieve().bodyToMono<List<Forhold>>().block()?.takeUnless(List<Forhold>::isEmpty)
        ?: throw NoTpOrdningerFoundException("No Tp-ordning found for person.")

    @Cacheable(TP_ORDNING_LEVERANDOR_CACHE)
    fun findTpLeverandor(tpOrdning: TPOrdning): String =
        webClient.get().uri("$tpUrl/api/tpconfig/tpleverandoer/${tpOrdning.tpId}").retrieve().bodyToMono<String>()
            .block()!!

    @Cacheable(TP_ORDNING_TSSID_CACHE)
    fun findTssId(tpId: String): String =
        webClient.get().uri("$tpUrl/api/tpconfig/tssnr/$tpId").retrieve().bodyToMono<String>().block()!!

}
