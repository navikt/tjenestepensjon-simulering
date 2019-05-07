package no.nav.tjenestepensjon.simulering;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.springframework.stereotype.Component;

@Component
public class AsyncExecutor<T extends Callable, S> {

    private final ExecutorService executorService;

    public AsyncExecutor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public List<S> executeAsync(List<T> callables) throws ExecutionException, InterruptedException {
        List<Future> futures = new ArrayList<>();
        callables.forEach(callable -> futures.add(executorService.submit(callable)));
        return getResult(futures);
    }

    private List<S> getResult(List<Future> futures) throws ExecutionException, InterruptedException {
        List<S> results = new ArrayList<>();
        for (Future future : futures) {
            results.add((S) future.get());
        }
        return results;
    }
}
