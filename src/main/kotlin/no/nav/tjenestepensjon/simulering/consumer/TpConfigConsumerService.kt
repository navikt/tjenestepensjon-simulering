package no.nav.tjenestepensjon.simulering.consumer

import no.nav.tjenestepensjon.simulering.config.CacheConfig
import no.nav.tjenestepensjon.simulering.config.WebClientConfig
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class TpConfigConsumerService : TpConfigConsumer {
    private var tpConfigUrl: String? = null
    private val webClient = WebClientConfig.webClient()
    @Value("\${TP_CONFIG_URL}")
    fun setTpConfigUrl(tpConfigUrl: String?) {
        this.tpConfigUrl = tpConfigUrl
    }

    @Cacheable(value = [CacheConfig.TP_ORDNING_LEVERANDOR_CACHE])
    override fun findTpLeverandor(tpOrdning: TPOrdning?) = webClient!!.get()
            .uri("$tpConfigUrl/tpleverandoer/${tpOrdning.tpId}")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
}