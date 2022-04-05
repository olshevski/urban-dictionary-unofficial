package dev.olshevski.udu.ui.common

import androidx.recyclerview.widget.DiffUtil
import dev.olshevski.udu.network.model.Definition

object DefinitionDiffCallback : DiffUtil.ItemCallback<Definition>() {

    override fun areItemsTheSame(oldItem: Definition, newItem: Definition): Boolean =
        oldItem.defId == newItem.defId

    override fun areContentsTheSame(oldItem: Definition, newItem: Definition): Boolean =
        oldItem == newItem

}