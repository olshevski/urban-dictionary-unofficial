package dev.olshevski.udu.domain

import dev.olshevski.udu.data.DefinitionsRepository
import dev.olshevski.udu.data.model.CallResult
import dev.olshevski.udu.domain.base.GetContentStateInteractor
import dev.olshevski.udu.network.model.Suggestion

class GetSearchSuggestions(private val definitionsRepository: DefinitionsRepository) :
    GetContentStateInteractor<String, List<Suggestion>>() {

    override suspend fun getContent(param: String): CallResult<List<Suggestion>> =
        definitionsRepository.getSearchSuggestions(param)

}