package dev.olshevski.udu.data

import androidx.annotation.MainThread
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dev.olshevski.udu.data.model.CallResult
import dev.olshevski.udu.data.paging.DefinitionsPagingSource
import dev.olshevski.udu.data.paging.RandomWordsRemoteMediator
import dev.olshevski.udu.data.paging.WordsOfTheDayRemoteMediator
import dev.olshevski.udu.network.UDApi
import dev.olshevski.udu.network.model.Definition
import dev.olshevski.udu.network.model.Direction
import dev.olshevski.udu.network.model.Suggestion
import dev.olshevski.udu.network.model.VoteRequest
import dev.olshevski.udu.util.retryApiCall
import org.koin.core.KoinComponent
import org.koin.core.get

/*
 * There isn't much work to be done in the repository, so everything is on main thread for
 * simplicity.
 *
 * Of course, network requests are suspended and executed on the worker thread.
 */
@MainThread
class DefinitionsRepository(
    private val udApi: UDApi,
    private val votesCache: VotesCache
) : KoinComponent {

    private companion object {
        val pagingConfig = PagingConfig(pageSize = 10, enablePlaceholders = false)
    }

    fun getWordsOfTheDay(): Pager<Int, Definition> {
        val definitionsCache = get<DefinitionsCache>()
        return Pager(
            config = pagingConfig,
            remoteMediator = WordsOfTheDayRemoteMediator(udApi, definitionsCache)
        ) {
            DefinitionsPagingSource(definitionsCache, votesCache)
        }
    }

    fun getRandomWords(): Pager<Int, Definition> {
        val definitionsCache = get<DefinitionsCache>()
        return Pager(
            config = pagingConfig,
            remoteMediator = RandomWordsRemoteMediator(udApi, definitionsCache)
        ) {
            DefinitionsPagingSource(definitionsCache, votesCache)
        }
    }

    suspend fun getDefinitions(term: String): CallResult<List<Definition>> = retryApiCall {
        udApi.getDefinitions(term).list
    }

    suspend fun getDefinitions(defId: Long): CallResult<List<Definition>> = retryApiCall {
        udApi.getDefinitions(defId).list
    }

    suspend fun getSearchSuggestions(term: String): CallResult<List<Suggestion>> = retryApiCall {
        udApi.getSearchSuggestions(term).results
    }

    suspend fun voteForDefinition(definition: Definition, direction: Direction) {
        votesCache.addVote(definition.defId, direction)
        retryApiCall {
            udApi.voteForDefinition(VoteRequest(definition.defId, direction))
        }
    }

}
