package dev.olshevski.udu.data.paging

import dev.olshevski.udu.data.DefinitionsCache
import dev.olshevski.udu.network.UDApi
import dev.olshevski.udu.util.retryApiCall

class WordsOfTheDayRemoteMediator(
    private val udApi: UDApi,
    definitionsCache: DefinitionsCache
) : DefinitionsRemoteMediator(definitionsCache, pageSize = 50) {

    override suspend fun loadPage(page: Int, pageSize: Int) = retryApiCall {
        udApi.getWordsOfTheDay(page, pageSize).list
    }

}