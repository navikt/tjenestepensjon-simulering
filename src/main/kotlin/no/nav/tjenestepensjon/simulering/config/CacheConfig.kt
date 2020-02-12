package no.nav.tjenestepensjon.simulering.config

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Caffeine.newBuilder
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.time.temporal.ChronoUnit.DAYS
import java.time.temporal.ChronoUnit.HOURS

@Configuration
@EnableCaching
class CacheConfig {
    internal val cacheList = listOf(
            getCache(TP_ORDNING_LEVERANDOR_CACHE, TP_ORDNING_LEVERANDOR_CACHE_EXPIRES),
            getCache(TP_ORDNING_PERSON_CACHE, TP_ORDNING_PERSON_CACHE_EXPIRES)
    )

    @Bean
    fun tpOrdningLeverandorCache() = getCache(TP_ORDNING_LEVERANDOR_CACHE, TP_ORDNING_LEVERANDOR_CACHE_EXPIRES)

    @Bean
    fun tpOrdningPersonCache() = getCache(TP_ORDNING_PERSON_CACHE, TP_ORDNING_PERSON_CACHE_EXPIRES)
    
    @Bean
    fun cacheManager() = SimpleCacheManager().apply {
        setCaches(cacheList)
    }

    companion object {
        fun getCache(name: String, duration: Duration?) = CaffeineCache(name, newBuilder()
                .recordStats()
                .expireUnlessNull(duration)
                .build())

        private fun <O, T> Caffeine<O,T>.expireUnlessNull(duration: Duration?) = if (duration == null) this else expireAfterWrite(duration)

        const val TP_ORDNING_LEVERANDOR_CACHE = "TP_ORDNING_LEVERANDOR_CACHE"
        val TP_ORDNING_LEVERANDOR_CACHE_EXPIRES: Duration = DAYS.duration
        const val TP_ORDNING_PERSON_CACHE = "TP_ORDNING_PERSON_CACHE"
        val TP_ORDNING_PERSON_CACHE_EXPIRES: Duration = HOURS.duration
    }
}