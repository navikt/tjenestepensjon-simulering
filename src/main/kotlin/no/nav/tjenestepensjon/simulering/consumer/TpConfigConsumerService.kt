package no.nav.tjenestepensjon.simulering.consumer

import no.nav.tjenestepensjon.simulering.config.CacheConfig
import no.nav.tjenestepensjon.simulering.config.WebClientConfig
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class TpConfigConsumerService(
        @Value("\${TP_CONFIG_URL}") val tpConfigUrl: String
) : TpConfigConsumer {
    private val webClient = WebClientConfig.webClient()

    @Cacheable(value = [CacheConfig.TP_ORDNING_LEVERANDOR_CACHE])
    override fun findTpLeverandor(tpOrdning: TPOrdning): String = webClient.get()
            .uri("$tpConfigUrl/tpleverandoer/${tpOrdning.tpId}")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()!!
}