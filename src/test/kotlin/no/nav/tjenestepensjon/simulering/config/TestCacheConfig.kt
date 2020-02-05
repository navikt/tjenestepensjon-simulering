package no.nav.tjenestepensjon.simulering.config

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import org.springframework.cache.Cache
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
internal class TestCacheConfig {
    @Bean
    fun testCacheable(): TestCacheable {
        return TestCacheable()
    }

    @Bean
    fun testCache(): Cache {
        return CaffeineCache(TEST_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .build())
    }

    @Bean
    fun testCacheExpires(): Cache {
        return CaffeineCache(TEST_CACHE_EXPIRES, Caffeine.newBuilder()
                .expireAfterWrite(SLEEP_TIME, TimeUnit.MILLISECONDS)
                .build())
    }

    internal class TestCacheable {
        @Cacheable(value = [TEST_CACHE])
        fun getCacheable(input: String?): String {
            try {
                Thread.sleep(SLEEP_TIME)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return "cached"
        }

        @Cacheable(value = [TEST_CACHE_EXPIRES])
        fun getCacheableExpires(input: String?): String {
            try {
                Thread.sleep(SLEEP_TIME)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return "cached"
        }
    }

    companion object {
        const val TEST_CACHE = "TEST_CACHE"
        const val TEST_CACHE_EXPIRES = "TEST_CACHE_EXPIRES"
        const val SLEEP_TIME = 250L
    }
}