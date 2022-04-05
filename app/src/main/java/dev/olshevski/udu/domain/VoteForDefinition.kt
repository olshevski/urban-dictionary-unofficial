package dev.olshevski.udu.domain

import dev.olshevski.udu.appScope
import dev.olshevski.udu.data.DefinitionsRepository
import dev.olshevski.udu.network.model.Definition
import dev.olshevski.udu.network.model.Direction
import kotlinx.coroutines.launch

class VoteForDefinition(private val definitionsRepository: DefinitionsRepository) {

    operator fun invoke(definition: Definition, direction: Direction) {
        appScope.launch {
            definitionsRepository.voteForDefinition(definition, direction)
        }
    }

}