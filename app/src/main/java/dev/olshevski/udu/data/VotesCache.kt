package dev.olshevski.udu.data

import androidx.annotation.MainThread
import dev.olshevski.udu.network.model.Direction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

@MainThread
class VotesCache {

    private val cache = MutableStateFlow(Snapshot(emptyMap()))
    val flow: Flow<Map<Long, Direction>> = cache.map { it.votes }
    val value get() = cache.value.votes

    private val weakInvalidationController = WeakInvalidationController(cache)

    fun addVote(defId: Long, direction: Direction) {
        cache.value = Snapshot(cache.value.votes + Pair(defId, direction))
    }

    fun addWeakInvalidationObserver(observer: InvalidationObserver) =
        weakInvalidationController.addWeakInvalidationObserver(observer)

    /**
     * Prevents MutableStateFlow from comparing all map entries for equality. Treat every update as
     * completely new data, even if items are exactly the same.
     *
     * This class doesn't override [equals] method and only compares actual object references.
     */
    private class Snapshot(val votes: Map<Long, Direction>)

}