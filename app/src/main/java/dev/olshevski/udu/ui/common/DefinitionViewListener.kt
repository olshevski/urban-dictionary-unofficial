package dev.olshevski.udu.ui.common

import dev.olshevski.udu.network.model.Definition
import dev.olshevski.udu.network.model.Direction

interface DefinitionViewListener {

    fun onLinkClicked(linkText: String)
    fun onThumbClicked(definition: Definition, direction: Direction)
    fun onShareClicked(definition: Definition)

}