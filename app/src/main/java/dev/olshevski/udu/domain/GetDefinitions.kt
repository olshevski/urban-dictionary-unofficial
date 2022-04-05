package dev.olshevski.udu.domain

import dev.olshevski.udu.data.DefinitionsRepository
import dev.olshevski.udu.data.VotesCache
import dev.olshevski.udu.data.model.CallResult
import dev.olshevski.udu.domain.base.GetContentStateInteractor
import dev.olshevski.udu.domain.base.model.ContentState
import dev.olshevski.udu.domain.base.model.map
import dev.olshevski.udu.network.model.Definition
import dev.olshevski.udu.network.model.updateVotes
import dev.olshevski.udu.ui.definitions.model.DefIdKey
import dev.olshevski.udu.ui.definitions.model.DefinitionKey
import dev.olshevski.udu.ui.definitions.model.TermKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetDefinitions(
    private val definitionsRepository: DefinitionsRepository,
    private val votesCache: VotesCache
) :
    GetContentStateInteractor<DefinitionKey, List<Definition>>() {

    override suspend fun getContent(param: DefinitionKey): CallResult<List<Definition>> =
        when (param) {
            is DefIdKey -> definitionsRepository.getDefinitions(param.defId)
            is TermKey -> definitionsRepository.getDefinitions(param.term)
        }

    override fun invoke(param: DefinitionKey): Flow<ContentState<List<Definition>>> =
        super.invoke(param).combine(votesCache.flow) { contentState, votes ->
            contentState.map { it.updateVotes(votes) }
        }

}