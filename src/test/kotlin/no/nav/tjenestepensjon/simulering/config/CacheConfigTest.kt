package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.config.CacheConfigTest.TestCache
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.lang.Thread.sleep
import java.time.Duration
import java.time.temporal.ChronoUnit.SECONDS
import java.util.Collections.singletonList
import kotlin.system.measureTimeMillis

@SpringBootTest(classes = [TestCache::class, CacheConfigTest.TestCacheConfig::class])
internal class CacheConfigTest {

    class TestCacheConfig: CacheConfig(){
        override val cacheList = singletonList(getCache(TEST_CACHE, TEST_CACHE_EXPIRES))
    }

    @Service
    class TestCache {
        @Cacheable(value = [TEST_CACHE])
        fun slowValueFetch(key: String) = "cached".also {
            sleep(DELAY)
        }

    }

    @Autowired
    lateinit var testCache: TestCache


//    @Test
    fun `Should cache responses`() {
        lateinit var old: String
        val uncachedDelay = measureTimeMillis {
            old = testCache.slowValueFetch(input)
        }
        assertNotNull(old)
        assert(DELAY <= uncachedDelay)

        lateinit var new: String
        val cachedDelay = measureTimeMillis {
            new = testCache.slowValueFetch(input)
        }
        assertEquals(old, new)
        assert(DELAY > cachedDelay)
    }

//    @Test
    @Throws(InterruptedException::class)
    fun `Cached responses should expire`() {
        lateinit var old: String
        val uncachedDelay = measureTimeMillis {
            old = testCache.slowValueFetch(input)
        }
        assertNotNull(old)
        assert(DELAY <= uncachedDelay)

        sleep(TEST_CACHE_EXPIRES.toMillis())

        lateinit var new: String
        val cachedDelay = measureTimeMillis {
            new = testCache.slowValueFetch(input)
        }
        assertEquals(old, new)
        assert(DELAY <= cachedDelay)
    }

    companion object {
        private const val input = "input"
        private const val DELAY = 250L
        private const val TEST_CACHE = "EXPIRING_TEST_CACHE"
        private val TEST_CACHE_EXPIRES: Duration = SECONDS.duration.multipliedBy(5)
    }
}