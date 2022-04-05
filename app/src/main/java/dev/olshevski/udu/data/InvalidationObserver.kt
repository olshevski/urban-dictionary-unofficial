package dev.olshevski.udu.data

import dev.olshevski.udu.appScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.ref.WeakReference

typealias InvalidationObserver = () -> Unit

class WeakInvalidationObserver(
    observer: InvalidationObserver,
    private val onWeakReferenceCleared: WeakInvalidationObserver.() -> Unit
) {

    private val weakRefObserver = WeakReference<InvalidationObserver>(observer)

    operator fun invoke() = weakRefObserver.get()?.invoke() ?: onWeakReferenceCleared()

}

class WeakInvalidationController(flow: Flow<*>) {

    private val observers = mutableListOf<WeakInvalidationObserver>()

    init {
        flow.onEach {
            // iterate over a copy of the list, because items in the original list will be removed
            // while iterating
            observers.toList().forEach { it.invoke() }
        }.launchIn(appScope)
    }

    /**
     * Reference to the [observer] must be kept externally. Otherwise it will be GC'ed prematurely.
     */
    fun addWeakInvalidationObserver(observer: InvalidationObserver) {
        observers += WeakInvalidationObserver(observer) {
            observers -= this
        }
    }

}