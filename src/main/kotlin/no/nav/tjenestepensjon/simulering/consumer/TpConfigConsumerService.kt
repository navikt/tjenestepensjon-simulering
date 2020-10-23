package no.nav.tjenestepensjon.simulering.consumer

import no.nav.tjenestepensjon.simulering.config.CacheConfig.Companion.TP_ORDNING_LEVERANDOR_CACHE
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class TpConfigConsumerService(
        @Value("\${TP_CONFIG_URL}") val tpConfigUrl: String,
        val webClient: WebClient
) : TpConfigConsumer {

    @Cacheable(TP_ORDNING_LEVERANDOR_CACHE)
    override fun findTpLeverandor(tpOrdning: TPOrdning): String = webClient.get()
            .uri("$tpConfigUrl/tpleverandoer/${tpOrdning.tpId}")
            .retrieve()
            .bodyToMono<String>()
            .block()!!
}