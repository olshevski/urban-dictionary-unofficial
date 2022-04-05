package dev.olshevski.udu.ui.definitions

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import dev.olshevski.udu.R
import dev.olshevski.udu.domain.base.model.ContentState
import dev.olshevski.udu.ui.common.DefaultDefinitionViewListener
import dev.olshevski.udu.ui.common.applyStyle
import dev.olshevski.udu.util.addRotationAgnosticLifecycleObserver
import dev.olshevski.udu.util.onStart
import dev.olshevski.udu.util.runInStartedScope
import dev.olshevski.udu.util.unsafeLazy
import dev.olshevski.udu.util.viewLifecycle
import kotlinx.android.synthetic.main.fragment_definitions.*
import kotlinx.android.synthetic.main.layout_definitions_common.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DefinitionsFragment : Fragment(R.layout.fragment_definitions) {

    private val viewModel by definitionsViewModel()

    /*
    ViewModel may be accessed only in onCreate, thus this adapter should be also created lazily.
     */
    private val definitionsAdapter by unsafeLazy {
        DefinitionsAdapter(
            DefaultDefinitionViewListener(viewModel)
        ).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    private val addDefinitionFooterAdapter = AddDefinitionFooterAdapter {
        viewModel.onAddDefinitionClicked()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        definitions.apply {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            adapter = ConcatAdapter(definitionsAdapter, addDefinitionFooterAdapter)
        }
        definitions_swipe_refresh_layout.apply {
            applyStyle()
            setOnRefreshListener {
                viewModel.onRefreshSwiped()
            }
        }
        retry_button.setOnClickListener {
            viewModel.onRetryButtonClicked()
        }
        add_definition_button.setOnClickListener {
            viewModel.onAddDefinitionClicked()
        }
        addRotationAgnosticLifecycleObserver(viewLifecycle,
            onStart {
                viewModel.onStart()
            }
        )
        viewLifecycle.runInStartedScope {
            viewModel.definitions.onEach { contentState ->
                progress_layout.isVisible = contentState is ContentState.Loading
                error_layout.isVisible = contentState is ContentState.Failed
                no_definitions_layout.isVisible =
                    contentState is ContentState.Loaded && contentState.content.definitions.isEmpty()
                definitions_swipe_refresh_layout.isVisible =
                    contentState is ContentState.Loaded && contentState.content.definitions.isNotEmpty()

                if (contentState is ContentState.Loaded) {
                    if (contentState.content.definitions.isEmpty()) {
                        /* TODO for now assume the term is never null, because we are not opening
                            screen by id. This functionality will be added later (maybe) */
                        no_definition_term.text = contentState.content.term!!
                    } else {
                        definitionsAdapter.submitList(contentState.content.definitions)
                    }
                }
            }.launchIn(this)
            viewModel.isSwipeRefreshing.onEach { isRefreshing ->
                definitions_swipe_refresh_layout.isRefreshing = isRefreshing
            }.launchIn(this)
        }
    }

}