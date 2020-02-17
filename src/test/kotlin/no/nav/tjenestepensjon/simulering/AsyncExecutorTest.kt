package no.nav.tjenestepensjon.simulering

import org.junit.jupiter.api.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

internal class AsyncExecutorTest {
    @Test
    fun isAsyncIfMultipleThreads() {
        val executor: AsyncExecutor<String, Sleepy> = AsyncExecutor(Executors.newFixedThreadPool(3))
        val startTime = System.currentTimeMillis()
        val results = executor.executeAsync(mapOf("1" to Sleepy(), "2" to Sleepy(), "3" to Sleepy()))
        val elapsed = System.currentTimeMillis() - startTime
        assertEquals(3, results.resultMap.size)
        assertTrue(elapsed < 300L)
    }

    @Test
    fun isSynchIfSingleThread() {
        val executor: AsyncExecutor<String, Sleepy> = AsyncExecutor(Executors.newFixedThreadPool(1))
        val startTime = System.currentTimeMillis()
        val results = executor.executeAsync(mapOf("1" to Sleepy(), "2" to Sleepy(), "3" to Sleepy()))
        val elapsed = System.currentTimeMillis() - startTime
        assertEquals(3, results.resultMap.size)
        assertTrue(elapsed > 300L)
    }

    @Test
    fun `Catch and return exceptions from async executions`() {
        val executor: AsyncExecutor<String, Sleepy> = AsyncExecutor(Executors.newFixedThreadPool(3))
        val results = executor.executeAsync(mapOf("1" to SleepyThrowsException(), "2" to SleepyThrowsException(), "3" to Sleepy()))
        assertEquals(1, results.resultMap.size)
        assertEquals(2, results.exceptions.size)
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