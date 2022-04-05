package dev.olshevski.udu.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.olshevski.udu.R
import dev.olshevski.udu.network.model.Suggestion
import kotlinx.android.synthetic.main.item_suggestion.view.*
import kotlin.properties.Delegates

class SuggestionsAdapter(
    private val suggestionClickedListener: (Suggestion) -> Unit
) : RecyclerView.Adapter<SuggestionsAdapter.SuggestionViewHolder>() {

    var suggestions: List<Suggestion> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder =
        SuggestionViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_suggestion, parent, false)
        )

    override fun getItemCount(): Int = suggestions.size

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(suggestions[position])
    }

    inner class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(suggestion: Suggestion) {
            itemView.setOnClickListener {
                suggestionClickedListener.invoke(suggestion)
            }
            itemView.term.text = suggestion.term
            itemView.preview.text = suggestion.preview
        }

    }
}