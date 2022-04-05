package dev.olshevski.udu.ui.definitions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.olshevski.udu.R
import kotlinx.android.synthetic.main.item_footer_add_definition.view.*

class AddDefinitionFooterAdapter(
    private val addDefinitionClickListener: () -> Unit
) : RecyclerView.Adapter<AddDefinitionFooterAdapter.AddDefinitionFooterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddDefinitionFooterViewHolder = AddDefinitionFooterViewHolder(parent)

    override fun onBindViewHolder(holder: AddDefinitionFooterViewHolder, position: Int) =
        holder.bind()

    override fun getItemCount(): Int = 1

    inner class AddDefinitionFooterViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_footer_add_definition, parent, false)
    ) {

        fun bind() {
            itemView.add_definition_button.setOnClickListener {
                addDefinitionClickListener.invoke()
            }
        }

    }
}