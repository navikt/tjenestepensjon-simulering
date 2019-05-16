package no.nav.tjenestepensjon.simulering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.springframework.stereotype.Component;

@Component
public class AsyncExecutor<Result, T extends Callable<Result>> {

    private final ExecutorService executorService;

    public AsyncExecutor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public <Key> AsyncResponse<Key, Result> executeAsync(Map<Key, T> callableMap) {
        Map<Key, Future<Result>> futureMap = new HashMap<>();
        callableMap.forEach((key, t) -> futureMap.put(key, executorService.submit(t)));
        AsyncResponse<Key, Result> response = new AsyncResponse<>();
        for (Map.Entry<Key, Future<Result>> entry : futureMap.entrySet()) {
            try {
                response.getResultMap().put(entry.getKey(), entry.getValue().get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                response.getExceptions().add(e);
            }
        }
        return response;
    }

    public static class AsyncResponse<Key, Result> {
        private Map<Key, Result> resultMap = new HashMap<>();
        private List<ExecutionException> exceptions = new ArrayList<>();

        public Map<Key, Result> getResultMap() {
            return resultMap;
        }

        public List<ExecutionException> getExceptions() {
            return exceptions;
        }
    }
}
