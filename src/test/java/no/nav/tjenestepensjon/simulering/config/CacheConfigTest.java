package no.nav.tjenestepensjon.simulering.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import static no.nav.tjenestepensjon.simulering.config.TestCacheConfig.SLEEP_TIME;
import static no.nav.tjenestepensjon.simulering.config.TestCacheConfig.TEST_CACHE;
import static no.nav.tjenestepensjon.simulering.config.TestCacheConfig.TEST_CACHE_EXPIRES;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import no.nav.tjenestepensjon.simulering.config.TestCacheConfig.TestCacheable;

@SpringBootTest(classes = {TestCacheConfig.class, CacheAutoConfiguration.class})
class CacheConfigTest {

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private TestCacheable testCacheable;

    @Test
    void shouldCacheResponses() {
        String input = "input";
        assertThat(cacheManager.getCache(TEST_CACHE), is(notNullValue()));
        assertThat(cacheManager.getCache(TEST_CACHE).get(input, String.class), is(nullValue()));

        long startTime = System.currentTimeMillis();
        String value = testCacheable.getCacheable(input);
        long endTime = System.currentTimeMillis() - startTime;
        String valueFromCache = cacheManager.getCache(TEST_CACHE).get(input, String.class);

        assertThat(endTime >= SLEEP_TIME, is(true));
        assertThat(valueFromCache, is(equalTo(value)));
        assertThat(valueFromCache, is("cached"));

        startTime = System.currentTimeMillis();
        String cached = testCacheable.getCacheable(input);
        endTime = System.currentTimeMillis() - startTime;
        assertThat(endTime < SLEEP_TIME, is(true));
        assertThat(cached, is("cached"));
    }

    @Test
    void shouldExpire() throws InterruptedException {
        String input = "input";
        assertThat(cacheManager.getCache(TEST_CACHE_EXPIRES).get(input, String.class), is(nullValue()));
        String cached = testCacheable.getCacheableExpires(input);
        assertThat(cacheManager.getCache(TEST_CACHE_EXPIRES).get(input, String.class), is(equalTo(cached)));
        Thread.sleep(SLEEP_TIME);
        assertThat(cacheManager.getCache(TEST_CACHE_EXPIRES).get(input, String.class), is(nullValue()));
    }
}