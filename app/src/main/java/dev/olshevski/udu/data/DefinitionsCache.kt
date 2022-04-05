package dev.olshevski.udu.data

import androidx.annotation.MainThread
import dev.olshevski.udu.network.model.Definition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

@MainThread
class DefinitionsCache {

    private val cache = MutableStateFlow(Snapshot(emptyList()))
    val flow: Flow<List<Definition>> = cache.map { it.definitions }
    val value get() = cache.value.definitions

    private val weakInvalidationController = WeakInvalidationController(cache)

    fun replace(definitions: List<Definition>) {
        cache.value = Snapshot(definitions)
    }

    fun add(definitions: List<Definition>) {
        cache.value = Snapshot(cache.value.definitions + definitions)
    }

    fun addWeakInvalidationObserver(observer: InvalidationObserver) =
        weakInvalidationController.addWeakInvalidationObserver(observer)

    /**
     * Prevents MutableStateFlow from comparing all list items for equality. Treat every update as
     * completely new data, even if items are exactly the same.
     *
     * This class doesn't override [equals] method and only compares actual object references.
     */
    private class Snapshot(val definitions: List<Definition>)

}