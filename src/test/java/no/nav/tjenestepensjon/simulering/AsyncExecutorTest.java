package no.nav.tjenestepensjon.simulering;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

class AsyncExecutorTest {

    @Test
    void isAsyncIfMultipleThreads() throws ExecutionException, InterruptedException {
        AsyncExecutor<Sleepy, String> executor = new AsyncExecutor<>(Executors.newFixedThreadPool(3));
        long startTime = System.currentTimeMillis();
        List<String> results = executor.executeAsync(List.of(new Sleepy(), new Sleepy(), new Sleepy()));
        long elapsed = System.currentTimeMillis() - startTime;
        assertThat(results.size(), is(3));
        assertThat(elapsed, is(lessThan(3 * 100L)));
    }

    @Test
    void isSynchIfSingleThread() throws ExecutionException, InterruptedException {
        AsyncExecutor<Sleepy, String> executor = new AsyncExecutor<>(Executors.newFixedThreadPool(1));
        long startTime = System.currentTimeMillis();
        List<String> results = executor.executeAsync(List.of(new Sleepy(), new Sleepy(), new Sleepy()));
        long elapsed = System.currentTimeMillis() - startTime;
        assertThat(results.size(), is(3));
        assertThat(elapsed, is(greaterThan(3 * 100L)));
    }

    private static class Sleepy implements Callable {

        @Override
        public Object call() throws Exception {
            Thread.sleep(100);
            return Thread.currentThread().getName();
        }
    }
}