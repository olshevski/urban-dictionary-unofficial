@file:Suppress("unused")

package dev.olshevski.udu.util

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.savedstate.SavedStateRegistry

fun ComponentActivity.addRotationAgnosticLifecycleObserver(
    lifecycle: Lifecycle,
    observer: LifecycleObserver
) = lifecycle.addRotationAgnosticLifecycleObserver(
    this,
    this.savedStateRegistry,
    observer
)

fun Fragment.addRotationAgnosticLifecycleObserver(
    lifecycle: Lifecycle,
    observer: LifecycleObserver
) = lifecycle.addRotationAgnosticLifecycleObserver(
    requireActivity(),
    this.savedStateRegistry,
    observer
)

private fun Lifecycle.addRotationAgnosticLifecycleObserver(
    activity: Activity,
    savedStateRegistry: SavedStateRegistry,
    observer: LifecycleObserver
) = addObserver(RotationAgnosticObserverWrapper(activity, savedStateRegistry, observer))

private class RotationAgnosticObserverWrapper(
    private val activity: Activity,
    private val savedStateRegistry: SavedStateRegistry,
    private val observer: LifecycleObserver
) : SavedStateRegistry.SavedStateProvider, LifecycleEventObserver {

    companion object {
        private const val KEY_SAVED_STATE = "rotation_agnostic_lifecycle_state"
        private const val KEY_LAST_DISPATCHED_STATE = "last_dispatched_state"
    }

    private var currentState = Lifecycle.State.INITIALIZED

    init {
        require(
            observer is DefaultLifecycleObserver
                    || observer is LifecycleEventObserver
        ) {
            """
            LifecycleObserver must implement either DefaultLifecycleObserver, or
            LifecycleEventObserver, or both. OnLifecycleEvent annotations are not supported.
            """.singleLine()
        }

        savedStateRegistry.registerSavedStateProvider(KEY_SAVED_STATE, this)
    }

    private fun restoreState() {
        restoreState(savedStateRegistry.consumeRestoredStateForKey(KEY_SAVED_STATE))
    }

    private fun restoreState(bundle: Bundle?) = bundle?.let {
        currentState = Lifecycle.State.values()[bundle.getInt(KEY_LAST_DISPATCHED_STATE)]
    }

    override fun saveState(): Bundle = Bundle().apply {
        putInt(KEY_LAST_DISPATCHED_STATE, currentState.ordinal)
    }

    override fun onStateChanged(owner: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_CREATE) {
            restoreState()
        }
        val nextState = event.toState()
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (event) {
            Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME -> {
                if (!currentState.isAtLeast(nextState)) {
                    currentState = nextState
                    observer.sendEvent(owner, event)
                }
            }
            Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_DESTROY -> {
                if (!activity.isChangingConfigurations) {
                    currentState = nextState
                    observer.sendEvent(owner, event)

                    if (event == Lifecycle.Event.ON_DESTROY) {
                        savedStateRegistry.unregisterSavedStateProvider(KEY_SAVED_STATE)
                    }
                }
            }
        }
    }

    private fun Lifecycle.Event.toState() = when (this) {
        Lifecycle.Event.ON_CREATE -> Lifecycle.State.CREATED
        Lifecycle.Event.ON_START -> Lifecycle.State.STARTED
        Lifecycle.Event.ON_RESUME -> Lifecycle.State.RESUMED
        Lifecycle.Event.ON_PAUSE -> Lifecycle.State.STARTED
        Lifecycle.Event.ON_STOP -> Lifecycle.State.CREATED
        Lifecycle.Event.ON_DESTROY -> Lifecycle.State.DESTROYED
        Lifecycle.Event.ON_ANY -> error("ON_ANY event is not supported")
    }

    private fun LifecycleObserver.sendEvent(owner: LifecycleOwner, event: Lifecycle.Event) {
        if (this is DefaultLifecycleObserver) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (event) {
                Lifecycle.Event.ON_CREATE -> onCreate(owner)
                Lifecycle.Event.ON_START -> onStart(owner)
                Lifecycle.Event.ON_RESUME -> onResume(owner)
                Lifecycle.Event.ON_PAUSE -> onPause(owner)
                Lifecycle.Event.ON_STOP -> onStop(owner)
                Lifecycle.Event.ON_DESTROY -> onDestroy(owner)
            }
        }
        if (this is LifecycleEventObserver) {
            onStateChanged(owner, event)
        }
    }

}