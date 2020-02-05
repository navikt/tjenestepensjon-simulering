package no.nav.tjenestepensjon.simulering.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.Cache
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    fun tpOrdningLeverandorCache(): Cache {
        return CaffeineCache(TP_ORDNING_LEVERANDOR_CACHE, Caffeine.newBuilder()
                .recordStats()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build())
    }

    @Bean
    fun tpOrdningPersonCache(): Cache {
        return CaffeineCache(TP_ORDNING_PERSON_CACHE, Caffeine.newBuilder()
                .recordStats()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build())
    }

    companion object {
        const val TP_ORDNING_LEVERANDOR_CACHE = "TP_ORDNING_LEVERANDOR_CACHE"
        const val TP_ORDNING_PERSON_CACHE = "TP_ORDNING_PERSON_CACHE"
    }
}