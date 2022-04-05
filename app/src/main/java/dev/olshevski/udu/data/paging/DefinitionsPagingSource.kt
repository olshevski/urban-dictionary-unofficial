package dev.olshevski.udu.data.paging

import dev.olshevski.udu.data.DefinitionsCache
import dev.olshevski.udu.data.VotesCache
import dev.olshevski.udu.data.paging.model.PageRange
import dev.olshevski.udu.network.model.Definition
import dev.olshevski.udu.network.model.updateVotes

open class DefinitionsPagingSource(
    private val definitionsCache: DefinitionsCache,
    private val votesCache: VotesCache
) : PositionalPagingSource<Definition>() {

    /*
    * Keep invalidationObserver reference in the class, so it is GC'ed only when this paging
    * source is not used anymore.
    */
    private val invalidationObserver = { invalidate() }

    init {
        votesCache.addWeakInvalidationObserver(invalidationObserver)
        definitionsCache.addWeakInvalidationObserver(invalidationObserver)
    }

    override suspend fun getTotalCount(): Int = definitionsCache.value.size

    override suspend fun loadRange(pageRange: PageRange): List<Definition> =
        definitionsCache.value
            .subList(pageRange.start, pageRange.end)
            .updateVotes(votesCache.value)

}
