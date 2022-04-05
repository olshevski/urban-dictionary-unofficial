package dev.olshevski.udu.ui.base.pageddefinitions

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import dev.olshevski.udu.network.model.Definition
import dev.olshevski.udu.ui.common.DefinitionDiffCallback
import dev.olshevski.udu.ui.common.DefinitionViewHolder
import dev.olshevski.udu.ui.common.DefinitionViewListener

class PagedDefinitionsAdapter(
    private val definitionViewListener: DefinitionViewListener
) : PagingDataAdapter<Definition, DefinitionViewHolder>(DefinitionDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefinitionViewHolder =
        DefinitionViewHolder(parent, definitionViewListener)

    override fun onBindViewHolder(holder: DefinitionViewHolder, position: Int) =
        holder.bind(getItem(position)!!)

}
