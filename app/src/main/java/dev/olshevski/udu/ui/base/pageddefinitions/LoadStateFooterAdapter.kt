package dev.olshevski.udu.ui.base.pageddefinitions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.olshevski.udu.R
import kotlinx.android.synthetic.main.item_footer_load_state.view.*

class LoadStateFooterAdapter(
    private val retryClickListener: () -> Unit
) : LoadStateAdapter<LoadStateFooterAdapter.LoadStateFooterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
        LoadStateFooterViewHolder(parent)

    override fun onBindViewHolder(holder: LoadStateFooterViewHolder, loadState: LoadState) =
        holder.bind(loadState)

    inner class LoadStateFooterViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_footer_load_state, parent, false)
    ) {

        fun bind(loadState: LoadState) {
            itemView.progress_layout.isVisible = loadState is LoadState.Loading
            itemView.error_layout.isVisible = loadState is LoadState.Error
            itemView.retry_button.setOnClickListener {
                retryClickListener.invoke()
            }
        }

    }

}
