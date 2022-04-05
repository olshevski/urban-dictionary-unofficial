package dev.olshevski.udu.ui.base.pageddefinitions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.cachedIn
import dev.olshevski.udu.domain.VoteForDefinition
import dev.olshevski.udu.domain.base.GetPagingDataInteractor
import dev.olshevski.udu.network.model.Definition
import dev.olshevski.udu.ui.base.pageddefinitions.model.PagingEvent
import dev.olshevski.udu.ui.common.DefinitionViewContract
import dev.olshevski.udu.ui.common.DefinitionViewContractImpl
import dev.olshevski.udu.ui.main.MainState
import dev.olshevski.udu.util.cacheIn
import dev.olshevski.udu.util.veto
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach

abstract class PagedDefinitionsViewModel(
    mainState: MainState,
    getPagingDataInteractor: GetPagingDataInteractor<Definition>,
    voteForDefinition: VoteForDefinition
) : ViewModel(),
    DefinitionViewContract by DefinitionViewContractImpl(mainState, voteForDefinition) {

    // note that here cachedIn is not the same as cacheIn
    val definitions = getPagingDataInteractor().cachedIn(viewModelScope)

    private val _definitionsLoadState = MutableStateFlow<LoadState?>(null)
    val definitionsLoadState: Flow<LoadState> = _definitionsLoadState
        .filterNotNull()
        .onEach { contentState ->
            if (contentState is LoadState.NotLoading) {
                _isSwipeRefreshing.value = false
            }
        }
        .veto { oldValue, newValue ->
            !(oldValue is LoadState.NotLoading && newValue is LoadState.Loading)
        }
        .cacheIn(viewModelScope)

    private val _pagingEvents: Channel<PagingEvent> = Channel(Channel.UNLIMITED)
    val pagingEvent: ReceiveChannel<PagingEvent> = _pagingEvents

    private val _isSwipeRefreshing = MutableStateFlow(false)
    val isSwipeRefreshing: Flow<Boolean> = _isSwipeRefreshing

    /*
     Need to pull back load states from the view to the view model layer so all the logic is here.
     */
    fun setLoadStates(loadStates: CombinedLoadStates) {
        _definitionsLoadState.value = loadStates.refresh
    }

    fun onRefreshSwiped() {
        _isSwipeRefreshing.value = true
        _pagingEvents.offer(PagingEvent.Refresh)
    }

    fun onRetryButtonClicked() {
        _pagingEvents.offer(PagingEvent.Retry)
    }

}
