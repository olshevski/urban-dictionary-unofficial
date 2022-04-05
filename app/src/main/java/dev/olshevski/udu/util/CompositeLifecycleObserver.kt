@file:Suppress("unused")

package dev.olshevski.udu.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

fun onCreate(block: (LifecycleOwner) -> Unit) =
    CompositeLifecycleObserver(Lifecycle.Event.ON_CREATE, block)

fun onStart(block: (LifecycleOwner) -> Unit) =
    CompositeLifecycleObserver(Lifecycle.Event.ON_START, block)

fun onResume(block: (LifecycleOwner) -> Unit) =
    CompositeLifecycleObserver(Lifecycle.Event.ON_RESUME, block)

fun onPause(block: (LifecycleOwner) -> Unit) =
    CompositeLifecycleObserver(Lifecycle.Event.ON_PAUSE, block)

fun onStop(block: (LifecycleOwner) -> Unit) =
    CompositeLifecycleObserver(Lifecycle.Event.ON_STOP, block)

fun onDestroy(block: (LifecycleOwner) -> Unit) =
    CompositeLifecycleObserver(Lifecycle.Event.ON_DESTROY, block)

class CompositeLifecycleObserver private constructor(
    private val observers: List<SingleEventObserver>
) : LifecycleEventObserver {

    constructor(event: Lifecycle.Event, block: (LifecycleOwner) -> Unit) : this(
        listOf(SingleEventObserver(event, block))
    )

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) =
        observers.filter { it.event == event }.forEach { it.block.invoke(source) }

    operator fun plus(other: CompositeLifecycleObserver) =
        CompositeLifecycleObserver(this.observers + other.observers)

    private class SingleEventObserver(
        val event: Lifecycle.Event,
        val block: (LifecycleOwner) -> Unit
    )

}
