package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.config.CacheConfig.Companion.getCache
import no.nav.tjenestepensjon.simulering.config.CacheConfigTest.TestCache
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import java.lang.Thread.sleep
import java.time.Duration
import java.time.temporal.ChronoUnit.SECONDS
import kotlin.system.measureTimeMillis

@SpringBootTest(classes = [TestCache::class])
internal class CacheConfigTest {

    @TestConfiguration
    @EnableCaching
    class TestCache {
        @Bean
        fun testCache() = getCache(TEST_CACHE, TEST_CACHE_EXPIRES)

        @Cacheable(value = [TEST_CACHE])
        fun slowValueFetch(key: String) = "cached".also {
            sleep(DELAY)
        }
    }

    @Autowired
    lateinit var testCache: TestCache

    @BeforeEach
    fun emptyCache() {
        sleep(TEST_CACHE_EXPIRES.toMillis())
    }

    @Test
    fun `Should cache responses`() {
        lateinit var old: String
        val uncachedDelay = measureTimeMillis {
            old = testCache.slowValueFetch(input)
        }
        assertNotNull(old)
        assert(DELAY <= uncachedDelay)

        val cachedDelay = measureTimeMillis {
            assertEquals(old, testCache.slowValueFetch(input))
        }
        assert(DELAY > cachedDelay)
    }

    @Test
    fun `Cached responses should expire`() {
        lateinit var old: String
        val uncachedDelay = measureTimeMillis {
            old = testCache.slowValueFetch(input)
        }
        assertNotNull(old)
        assert(DELAY <= uncachedDelay)

        sleep(TEST_CACHE_EXPIRES.toMillis())

        val cachedDelay = measureTimeMillis {
            assertEquals(old, testCache.slowValueFetch(input))
        }
        assert(DELAY <= cachedDelay)
    }

    companion object {
        private const val input = "input"
        internal const val DELAY = 250L
        internal const val TEST_CACHE = "EXPIRING_TEST_CACHE"
        internal val TEST_CACHE_EXPIRES: Duration = SECONDS.duration
    }
}
