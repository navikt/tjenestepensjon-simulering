package no.nav.tjenestepensjon.simulering

import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

@Component
class AsyncExecutor<Result, T : Callable<Result>>(private val executorService: ExecutorService) {
    fun <Key> executeAsync(callableMap: Map<Key, T>): AsyncResponse<Key, Result> {
        val futureMap: Map<Key, Future<Result>> = callableMap.mapValues { (_, t) -> executorService.submit(t) }
        val response = AsyncResponse<Key, Result>()
        futureMap.forEach { (key, value) ->
            try {
                response.resultMap[key] = value.get()
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            } catch (e: ExecutionException) {
                response.exceptions.add(e)
            }
        }
        return response
    }

    class AsyncResponse<Key, Result> {
        val resultMap: MutableMap<Key, Result> = HashMap()
        val exceptions: MutableList<ExecutionException> = ArrayList()
    }

}