package no.nav.tjenestepensjon.simulering;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

class AsyncExecutorTest {

    @Test
    void isAsyncIfMultipleThreads() {
        AsyncExecutor<String, Sleepy> executor = new AsyncExecutor<>(Executors.newFixedThreadPool(3));
        long startTime = System.currentTimeMillis();
        AsyncExecutor.AsyncResponse results = executor.executeAsync(Map.of("1", new Sleepy(), "2", new Sleepy(), "3", new Sleepy()));
        long elapsed = System.currentTimeMillis() - startTime;
        assertThat(results.getResultMap().size(), is(3));
        assertThat(elapsed, is(lessThan(3 * 100L)));
    }

    @Test
    void isSynchIfSingleThread() {
        AsyncExecutor<String, Sleepy> executor = new AsyncExecutor<>(Executors.newFixedThreadPool(1));
        long startTime = System.currentTimeMillis();
        AsyncExecutor.AsyncResponse results = executor.executeAsync(Map.of("1", new Sleepy(), "2", new Sleepy(), "3", new Sleepy()));
        long elapsed = System.currentTimeMillis() - startTime;
        assertThat(results.getResultMap().size(), is(3));
        assertThat(elapsed, is(greaterThan(3 * 100L)));
    }

    @Test
    void catchAndReturnExceptionsFromAsyncExecutions() {
        AsyncExecutor<String, Sleepy> executor = new AsyncExecutor<>(Executors.newFixedThreadPool(3));
        AsyncExecutor.AsyncResponse results = executor.executeAsync(Map.of("1", new SleepyThrowsException(), "2", new SleepyThrowsException(), "3", new Sleepy()));
        assertThat(results.getResultMap().size(), is(1));
        assertThat(results.getExceptions().size(), is(2));
    }

    private static class Sleepy implements Callable<String> {
        @Override
        public String call() throws Exception {
            Thread.sleep(100);
            return Thread.currentThread().getName();
        }
    }

    private static class SleepyThrowsException extends Sleepy implements Callable<String> {
        @Override
        public String call() {
            throw new RuntimeException("excpetion");
        }
    }
}