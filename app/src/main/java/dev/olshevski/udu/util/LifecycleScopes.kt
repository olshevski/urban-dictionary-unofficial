@file:Suppress("unused")

package dev.olshevski.udu.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

fun Lifecycle.runInCreatedScope(block: CoroutineScope.() -> Unit) =
    runInScope(Lifecycle.Event.ON_CREATE, block)

fun Lifecycle.runInStartedScope(block: CoroutineScope.() -> Unit) =
    runInScope(Lifecycle.Event.ON_START, block)

fun Lifecycle.runInResumedScope(block: CoroutineScope.() -> Unit) =
    runInScope(Lifecycle.Event.ON_RESUME, block)

private fun Lifecycle.runInScope(enterEvent: Lifecycle.Event, block: CoroutineScope.() -> Unit) {
    check(currentState != Lifecycle.State.DESTROYED) {
        "are you sure you want to do this in DESTROYED state?"
    }
    addObserver(ScopeController(enterEvent, block))
}

private class ScopeController(
    private val enterEvent: Lifecycle.Event,
    private val block: CoroutineScope.() -> Unit
) : LifecycleEventObserver {

    private val exitEvent = when (enterEvent) {
        Lifecycle.Event.ON_CREATE -> Lifecycle.Event.ON_DESTROY
        Lifecycle.Event.ON_START -> Lifecycle.Event.ON_STOP
        Lifecycle.Event.ON_RESUME -> Lifecycle.Event.ON_PAUSE
        else -> error("unsupported enter event $enterEvent")
    }

    private var coroutineScope: CoroutineScope? = null

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (event) {
            enterEvent -> {
                check(coroutineScope == null)
                coroutineScope =
                    CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).apply(block)
            }
            exitEvent -> {
                check(coroutineScope != null)
                coroutineScope!!.cancel()
                coroutineScope = null
            }
        }
    }

}