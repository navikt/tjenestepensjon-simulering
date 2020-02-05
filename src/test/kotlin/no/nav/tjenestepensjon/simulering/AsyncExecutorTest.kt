package no.nav.tjenestepensjon.simulering

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executors

internal class AsyncExecutorTest {
    @get:Test
    val isAsyncIfMultipleThreads: Unit
        get() {
            val executor: AsyncExecutor<String, Sleepy> = AsyncExecutor(Executors.newFixedThreadPool(3))
            val startTime = System.currentTimeMillis()
            val results: AsyncResponse = executor.executeAsync(Map.of("1", Sleepy(), "2", Sleepy(), "3", Sleepy()))
            val elapsed = System.currentTimeMillis() - startTime
            assertThat(results.getResultMap().size(), Matchers.`is`(3))
            MatcherAssert.assertThat(elapsed, Matchers.`is`(Matchers.lessThan(3 * 100L)))
        }

    @get:Test
    val isSynchIfSingleThread: Unit
        get() {
            val executor: AsyncExecutor<String, Sleepy> = AsyncExecutor(Executors.newFixedThreadPool(1))
            val startTime = System.currentTimeMillis()
            val results: AsyncResponse = executor.executeAsync(Map.of("1", Sleepy(), "2", Sleepy(), "3", Sleepy()))
            val elapsed = System.currentTimeMillis() - startTime
            assertThat(results.getResultMap().size(), Matchers.`is`(3))
            MatcherAssert.assertThat(elapsed, Matchers.`is`(Matchers.greaterThan(3 * 100L)))
        }

    @Test
    fun catchAndReturnExceptionsFromAsyncExecutions() {
        val executor: AsyncExecutor<String, Sleepy> = AsyncExecutor(Executors.newFixedThreadPool(3))
        val results: AsyncResponse = executor.executeAsync(Map.of("1", SleepyThrowsException(), "2", SleepyThrowsException(), "3", Sleepy()))
        assertThat(results.getResultMap().size(), Matchers.`is`(1))
        assertThat(results.getExceptions().size(), Matchers.`is`(2))
    }

    private open class Sleepy : Callable<String> {
        @Throws(Exception::class)
        override fun call(): String {
            Thread.sleep(100)
            return Thread.currentThread().name
        }
    }

    private class SleepyThrowsException : Sleepy(), Callable<String> {
        override fun call(): String {
            throw RuntimeException("exception")
        }
    }
}