package dev.olshevski.udu.ui.common

import dev.olshevski.udu.network.model.Definition
import dev.olshevski.udu.network.model.Direction

class DefaultDefinitionViewListener(private val vmContract: DefinitionViewContract) :
    DefinitionViewListener {

    override fun onLinkClicked(linkText: String) {
        vmContract.onLinkClicked(linkText)
    }

    override fun onThumbClicked(definition: Definition, direction: Direction) {
        vmContract.onThumbClicked(definition, direction)
    }

    override fun onShareClicked(definition: Definition) {
        vmContract.onShareClicked(definition)
    }

}