package no.nav.tjenestepensjon.simulering.consumer

import no.nav.tjenestepensjon.simulering.config.CacheConfig
import no.nav.tjenestepensjon.simulering.config.WebClientConfig
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Service

@Service
class TpRegisterConsumerService(private val tokenClient: TokenClient) : TpRegisterConsumer {
    private var tpRegisterUrl: String? = null
    private val webClient = WebClientConfig.webClient()
    @Value("\${TP_REGISTERET_URL}")
    fun setTpRegisterUrl(tpRegisterUrl: String?) {
        this.tpRegisterUrl = tpRegisterUrl
    }

    @Cacheable(value = [CacheConfig.TP_ORDNING_PERSON_CACHE])
    @Throws(NoTpOrdningerFoundException::class)
    override fun getTpOrdningerForPerson(fnr: FNR): List<TPOrdning> {
        return webClient.get()
                .uri("$tpRegisterUrl/person/$fnr/tpordninger")
                .header(AUTHORIZATION, "Bearer " + tokenClient.oidcAccessToken.accessToken)
                .retrieve()
                .bodyToMono(object : ParameterizedTypeReference<List<TPOrdning>>() {})
                .block()
                .takeUnless(List<TPOrdning>::isEmpty)
                ?: throw NoTpOrdningerFoundException("No Tp-ordning found for person:$fnr")
    }

}