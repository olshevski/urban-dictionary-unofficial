package dev.olshevski.udu.data.paging

import dev.olshevski.udu.data.DefinitionsCache
import dev.olshevski.udu.network.UDApi
import dev.olshevski.udu.util.retryApiCall

class RandomWordsRemoteMediator(
    private val udApi: UDApi,
    definitionsCache: DefinitionsCache
) : DefinitionsRemoteMediator(
    definitionsCache,
    pageSize = 10
) {

    override suspend fun loadPage(page: Int, pageSize: Int) = retryApiCall {
        udApi.getRandomWords().list.also {
            // random words request always returns 10 definitions
            check(it.size == pageSize)
        }
    }

}