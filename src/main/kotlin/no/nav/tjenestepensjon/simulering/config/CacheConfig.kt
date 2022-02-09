package no.nav.tjenestepensjon.simulering.config

import com.github.benmanes.caffeine.cache.Caffeine.newBuilder
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.time.temporal.ChronoUnit.DAYS
import java.time.temporal.ChronoUnit.HOURS

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun tpOrdningLeverandorCache() = getCache(TP_ORDNING_LEVERANDOR_CACHE, TP_ORDNING_LEVERANDOR_CACHE_EXPIRES)

    @Bean
    fun tpOrdningTssIdCache() = getCache(TP_ORDNING_TSSID_CACHE, TP_ORDNING_TSSID_CACHE_EXPIRES)

    @Bean
    fun tpOrdningPersonCache() = getCache(TP_ORDNING_PERSON_CACHE, TP_ORDNING_PERSON_CACHE_EXPIRES)

    companion object {
        fun getCache(name: String, duration: Duration) = CaffeineCache(
            name, newBuilder().recordStats().expireAfterWrite(duration).build()
        )

        const val TP_ORDNING_LEVERANDOR_CACHE = "TP_ORDNING_LEVERANDOR_CACHE"
        val TP_ORDNING_LEVERANDOR_CACHE_EXPIRES: Duration = DAYS.duration
        const val TP_ORDNING_TSSID_CACHE = "TP_ORDNING_TSSID_CACHE"
        val TP_ORDNING_TSSID_CACHE_EXPIRES: Duration = DAYS.duration
        const val TP_ORDNING_PERSON_CACHE = "TP_ORDNING_PERSON_CACHE"
        val TP_ORDNING_PERSON_CACHE_EXPIRES: Duration = HOURS.duration
    }
}
