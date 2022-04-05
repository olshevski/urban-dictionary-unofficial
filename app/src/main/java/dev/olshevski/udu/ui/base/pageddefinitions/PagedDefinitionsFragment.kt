package dev.olshevski.udu.ui.base.pageddefinitions

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import dev.olshevski.udu.R
import dev.olshevski.udu.ui.base.pageddefinitions.model.PagingEvent
import dev.olshevski.udu.ui.common.DefaultDefinitionViewListener
import dev.olshevski.udu.ui.common.applyStyle
import dev.olshevski.udu.util.runInStartedScope
import dev.olshevski.udu.util.unsafeLazy
import dev.olshevski.udu.util.viewLifecycle
import kotlinx.android.synthetic.main.layout_definitions_common.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class PagedDefinitionsFragment : Fragment(R.layout.fragment_paged_definitions) {

    protected abstract val viewModel: PagedDefinitionsViewModel

    /*
    ViewModel may be accessed only in onCreate, thus this adapter should be also created lazily.
     */
    private val pagedDefinitionsAdapter by unsafeLazy {
        PagedDefinitionsAdapter(
            DefaultDefinitionViewListener(viewModel)
        ).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    private val loadStateFooterAdapter = LoadStateFooterAdapter {
        viewModel.onRetryButtonClicked()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        definitions.apply {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            adapter = pagedDefinitionsAdapter.withLoadStateFooter(loadStateFooterAdapter)
        }
        definitions_swipe_refresh_layout.apply {
            applyStyle()
            setOnRefreshListener { viewModel.onRefreshSwiped() }
        }
        retry_button.setOnClickListener {
            viewModel.onRetryButtonClicked()
        }
        viewLifecycle.runInStartedScope {
            launch {
                for (event in viewModel.pagingEvent) {
                    when (event) {
                        is PagingEvent.Retry -> pagedDefinitionsAdapter.retry()
                        is PagingEvent.Refresh -> pagedDefinitionsAdapter.refresh()
                    }
                }
            }
            viewModel.definitions.onEach { pagingData ->
                pagedDefinitionsAdapter.submitData(pagingData)
            }.launchIn(this)
            pagedDefinitionsAdapter.loadStateFlow.onEach { loadStates ->
                viewModel.setLoadStates(loadStates)
            }.launchIn(this)
            viewModel.definitionsLoadState.onEach { loadState ->
                definitions_swipe_refresh_layout.isVisible = loadState is LoadState.NotLoading
                progress_layout.isVisible = loadState is LoadState.Loading
                error_layout.isVisible = loadState is LoadState.Error
            }.launchIn(this)
            viewModel.isSwipeRefreshing.onEach { isRefreshing ->
                definitions_swipe_refresh_layout.isRefreshing = isRefreshing
            }.launchIn(this)
        }
    }

}