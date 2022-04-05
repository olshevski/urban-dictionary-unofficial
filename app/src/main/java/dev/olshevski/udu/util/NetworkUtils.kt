package dev.olshevski.udu.util

import dev.olshevski.udu.data.model.CallResult
import kotlinx.coroutines.delay
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

suspend fun <T> safeApiCall(block: suspend () -> T): CallResult<T> = try {
    CallResult.Success(block())
} catch (e: HttpException) {
    CallResult.Error(e)
} catch (e: IOException) {
    CallResult.Error(e)
}

suspend fun <T> retryApiCall(
    times: Int = 5,
    initialDelay: Long = 250, // 0.25 second
    maxDelay: Long = 4000,    // 4 second
    factor: Double = 2.0,
    block: suspend () -> T
): CallResult<T> {
    var currentDelay = initialDelay
    repeat(times) { attempt ->
        val callResult = safeApiCall(block)
        when {
            callResult is CallResult.Success || attempt == times - 1 -> return callResult
            callResult is CallResult.Error -> Timber.d(callResult.exception)
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    error("unreachable code")
}