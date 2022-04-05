package dev.olshevski.udu.ui.common

import androidx.lifecycle.ViewModel
import dev.olshevski.udu.NavGraphDirections
import dev.olshevski.udu.domain.VoteForDefinition
import dev.olshevski.udu.network.model.Definition
import dev.olshevski.udu.network.model.Direction
import dev.olshevski.udu.ui.definitions.model.TermKey
import dev.olshevski.udu.ui.main.MainState

/**
 * Common view model functionality for all DefinitionViewHolder related operations.
 *
 * All ViewModel's will delegate to this implementation.
 */
open class DefinitionViewContractImpl(
    private val mainState: MainState,
    private val voteForDefinition: VoteForDefinition
) : ViewModel(), DefinitionViewContract {

    override fun onLinkClicked(linkText: String) {
        mainState.navigate(NavGraphDirections.toDefinitionsFragment(TermKey(linkText)))
    }

    override fun onThumbClicked(definition: Definition, direction: Direction) {
        voteForDefinition(definition, direction)
    }

    override fun onShareClicked(definition: Definition) {
        mainState.shareLink(definition.permalink)
    }

}