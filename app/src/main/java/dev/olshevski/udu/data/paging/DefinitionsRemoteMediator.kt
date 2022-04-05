package dev.olshevski.udu.data.paging

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dev.olshevski.udu.data.DefinitionsCache
import dev.olshevski.udu.data.model.CallResult
import dev.olshevski.udu.network.model.Definition

abstract class DefinitionsRemoteMediator(
    private val definitionsCache: DefinitionsCache,
    private val pageSize: Int
) : RemoteMediator<Int, Definition>() {

    private companion object {
        const val FIRST_PAGE_INDEX = 1
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Definition>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.REFRESH -> FIRST_PAGE_INDEX
            LoadType.APPEND -> {
                val count = cachedPagesCount()

                /*
                 When REFRESH failed to load any data, RemoteMediator still tries to load next page
                 with APPEND loadType. Prevent it with this check.

                 This is probably some bug in the version 3.0.0-alpha05 of Paging library.
                 */
                if (count == 0) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                } else {
                    count + 1
                }
            }
        }
        return when (val result = loadPage(page, pageSize)) {
            is CallResult.Success -> {
                val data = result.data
                if (loadType == LoadType.REFRESH) {
                    definitionsCache.replace(data)
                } else {
                    definitionsCache.add(data)
                }
                MediatorResult.Success(endOfPaginationReached = data.size < pageSize)
            }
            is CallResult.Error -> MediatorResult.Error(result.exception)
        }
    }

    private fun cachedPagesCount() = definitionsCache.value.size / pageSize

    abstract suspend fun loadPage(page: Int, pageSize: Int): CallResult<List<Definition>>

}