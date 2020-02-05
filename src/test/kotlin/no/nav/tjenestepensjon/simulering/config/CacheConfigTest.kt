package no.nav.tjenestepensjon.simulering.config

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.config.TestCacheConfig.TestCacheable
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager

@SpringBootTest(classes = [TestCacheConfig::class, CacheAutoConfiguration::class])
internal class CacheConfigTest {
    @Autowired
    private val cacheManager: CacheManager? = null
    @Autowired
    private val testCacheable: TestCacheable? = null

    @Test
    fun shouldCacheResponses() {
        val input = "input"
        MatcherAssert.assertThat(cacheManager!!.getCache(TestCacheConfig.Companion.TEST_CACHE), Matchers.`is`(Matchers.notNullValue()))
        MatcherAssert.assertThat(cacheManager.getCache(TestCacheConfig.Companion.TEST_CACHE).get(input, String::class.java), Matchers.`is`(Matchers.nullValue()))
        var startTime = System.currentTimeMillis()
        val value = testCacheable!!.getCacheable(input)
        var endTime = System.currentTimeMillis() - startTime
        val valueFromCache = cacheManager.getCache(TestCacheConfig.Companion.TEST_CACHE).get(input, String::class.java)
        MatcherAssert.assertThat(endTime >= TestCacheConfig.Companion.SLEEP_TIME, Matchers.`is`(true))
        MatcherAssert.assertThat(valueFromCache, Matchers.`is`(Matchers.equalTo(value)))
        MatcherAssert.assertThat(valueFromCache, Matchers.`is`("cached"))
        startTime = System.currentTimeMillis()
        val cached = testCacheable.getCacheable(input)
        endTime = System.currentTimeMillis() - startTime
        MatcherAssert.assertThat(endTime < TestCacheConfig.Companion.SLEEP_TIME, Matchers.`is`(true))
        MatcherAssert.assertThat(cached, Matchers.`is`("cached"))
    }

    @Test
    @Throws(InterruptedException::class)
    fun shouldExpire() {
        val input = "input"
        MatcherAssert.assertThat(cacheManager!!.getCache(TestCacheConfig.Companion.TEST_CACHE_EXPIRES).get(input, String::class.java), Matchers.`is`(Matchers.nullValue()))
        val cached = testCacheable!!.getCacheableExpires(input)
        MatcherAssert.assertThat(cacheManager.getCache(TestCacheConfig.Companion.TEST_CACHE_EXPIRES).get(input, String::class.java), Matchers.`is`(Matchers.equalTo(cached)))
        Thread.sleep(TestCacheConfig.Companion.SLEEP_TIME)
        MatcherAssert.assertThat(cacheManager.getCache(TestCacheConfig.Companion.TEST_CACHE_EXPIRES).get(input, String::class.java), Matchers.`is`(Matchers.nullValue()))
    }
}