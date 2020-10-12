package no.nav.tjenestepensjon.simulering.consumer

import no.nav.tjenestepensjon.simulering.config.CacheConfig
import no.nav.tjenestepensjon.simulering.config.WebClientConfig
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v2.consumer.TokenClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class TpRegisterConsumerService(private val tokenClient: TokenClient) : TpRegisterConsumer {
    @Value("\${TP_REGISTERET_URL}")
    lateinit var tpregisteretUrl: String
    private val webClient = WebClientConfig.webClient()

    @Cacheable(value = [CacheConfig.TP_ORDNING_PERSON_CACHE])
    @Throws(NoTpOrdningerFoundException::class)
    override fun getTpOrdningerForPerson(fnr: FNR): List<TPOrdning> {
        return webClient.get()
                .uri("$tpregisteretUrl/person/tpordninger")
                .header(AUTHORIZATION, "Bearer " + tokenClient.tpregisteretToken)
                .header("fnr", fnr.toString())
                .retrieve()
                .bodyToMono<List<TPOrdning>>()
                .block()
                .takeUnless(List<TPOrdning>::isEmpty)
                ?: throw NoTpOrdningerFoundException("No Tp-ordning found for person:$fnr")
    }

}