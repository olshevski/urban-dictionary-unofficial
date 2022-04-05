@file:Suppress("unused")

package dev.olshevski.udu.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import java.util.concurrent.atomic.AtomicBoolean

fun <T> Flow<T>.log(tag: String, logLevel: Int = Log.VERBOSE) = this
    .onStart { Log.println(logLevel, tag, "onStart") }
    .onEach { Log.println(logLevel, tag, "onEach $it") }
    .onCompletion { Log.println(logLevel, tag, "onCompletion") }
    .catch {
        Log.println(logLevel, tag, "catch\n${Log.getStackTraceString(it)}")
        throw it
    }

fun <T> Flow<T>.assertMainThread() = onStart {
    checkMainThread { "Flow is not running on main thread" }
}

fun <T> Flow<T>.assertCollectedOnlyOnce(): Flow<T> {
    val collectStarted = AtomicBoolean(false)
    return this.onStart {
        if (!collectStarted.compareAndSet(false, true)) {
            throw error("This flow may be collected only once")
        }
    }
}

/** Special initial value that can be safely filtered afterwards */
private object NotSet

private fun <T> Flow<Any?>.filterNotSetValue() = transform {
    if (it !== NotSet) {
        @Suppress("UNCHECKED_CAST")
        emit(it as T)
    }
}

fun <T> Flow<T>.cacheIn(coroutineScope: CoroutineScope): Flow<T> =
    MutableStateFlow<Any?>(NotSet)
        .apply {
            this@cacheIn.onEach { this.value = it }.launchIn(coroutineScope)
        }
        .filterNotSetValue()

fun <T> Flow<T>.stateIn(coroutineScope: CoroutineScope, initialValue: T): StateFlow<T> =
    MutableStateFlow(initialValue)
        .apply {
            this@stateIn.onEach { this.value = it }.launchIn(coroutineScope)
        }

fun <T> Flow<T>.veto(initialValue: T, onChange: (oldValue: T, newValue: T) -> Boolean) = flow {
    var currentValue: T = initialValue
    emit(currentValue)
    collect { value ->
        if (onChange(currentValue, value)) {
            currentValue = value
            emit(value)
        }
    }
}

fun <T> Flow<T>.veto(onChange: (oldValue: T, newValue: T) -> Boolean) =
    veto(NotSet) { oldValue, newValue ->
        @Suppress("UNCHECKED_CAST")
        oldValue === NotSet || onChange(oldValue as T, newValue as T)
    }.filterNotSetValue<T>()