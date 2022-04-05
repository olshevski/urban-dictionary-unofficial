package dev.olshevski.udu.ui.definitions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import dev.olshevski.udu.domain.GetDefinitions
import dev.olshevski.udu.domain.VoteForDefinition
import dev.olshevski.udu.domain.base.model.ContentState
import dev.olshevski.udu.domain.base.model.map
import dev.olshevski.udu.ui.common.DefinitionViewContract
import dev.olshevski.udu.ui.common.DefinitionViewContractImpl
import dev.olshevski.udu.ui.common.defineWordUrlWithHint
import dev.olshevski.udu.ui.definitions.model.DefinitionData
import dev.olshevski.udu.ui.definitions.model.TermKey
import dev.olshevski.udu.ui.main.MainState
import dev.olshevski.udu.ui.main.mainActivity
import dev.olshevski.udu.ui.main.mainState
import dev.olshevski.udu.ui.main.model.Screen
import dev.olshevski.udu.util.cacheIn
import dev.olshevski.udu.util.veto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

fun DefinitionsFragment.definitionsViewModel() = viewModel<DefinitionsViewModel> {
    parametersOf(navArgs<DefinitionsFragmentArgs>().value, mainActivity.mainState().value)
}

class DefinitionsViewModel(
    private val definitionFragmentArgs: DefinitionsFragmentArgs,
    private val mainState: MainState,
    getDefinitions: GetDefinitions,
    voteForDefinition: VoteForDefinition
) : ViewModel(),
    DefinitionViewContract by DefinitionViewContractImpl(mainState, voteForDefinition) {

    private val term = (definitionFragmentArgs.definitionKey as? TermKey)?.term

    private val restartFlow = MutableStateFlow(RestartSignal())

    val definitions = restartFlow
        .flatMapLatest { getDefinitions(definitionFragmentArgs.definitionKey) }
        .onEach { contentState ->
            if (contentState is ContentState.Loaded) {
                _isSwipeRefreshing.value = false
            }
        }
        .veto { oldValue, newValue ->
            !(oldValue is ContentState.Loaded && newValue is ContentState.Loading)
        }
        .map {
            it.map { definitions -> DefinitionData(term, definitions) }
        }
        .cacheIn(viewModelScope)

    private val _isSwipeRefreshing = MutableStateFlow(false)
    val isSwipeRefreshing: Flow<Boolean> = _isSwipeRefreshing

    fun onStart() {
        mainState.setScreen(Screen.Definition(term))
    }

    fun onRefreshSwiped() {
        _isSwipeRefreshing.value = true
        restartFlow.value = RestartSignal()
    }

    fun onRetryButtonClicked() {
        restartFlow.value = RestartSignal()
    }

    fun onAddDefinitionClicked() {
        mainState.openWebpage(defineWordUrlWithHint(term!!))
    }

    class RestartSignal
}