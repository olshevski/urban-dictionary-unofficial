package dev.olshevski.udu.ui.main

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import dev.olshevski.blueprint.ui.NavigationEvent
import dev.olshevski.udu.NavGraphDirections
import dev.olshevski.udu.domain.GetSearchSuggestions
import dev.olshevski.udu.domain.base.model.ContentState
import dev.olshevski.udu.network.model.Suggestion
import dev.olshevski.udu.ui.common.DEFINE_WORD_URL
import dev.olshevski.udu.ui.definitions.model.TermKey
import dev.olshevski.udu.ui.main.model.Screen
import dev.olshevski.udu.ui.main.model.ToolbarState
import dev.olshevski.udu.util.cacheIn
import dev.olshevski.udu.util.stateFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import org.koin.androidx.viewmodel.ext.android.stateViewModel

private fun MainActivity.mainViewModel() = stateViewModel<MainViewModel>()

fun MainActivity.mainContract() = mainViewModel() as Lazy<MainContract>

fun MainActivity.mainState() = mainViewModel() as Lazy<MainState>

class MainViewModel(
    savedStateHandle: SavedStateHandle,
    getSearchSuggestions: GetSearchSuggestions
) : ViewModel(), MainContract, MainState {

    companion object {
        private const val EMPTY = ""
    }

    private val _navigationEvents: Channel<NavigationEvent> = Channel(Channel.UNLIMITED)
    override val navigationEvents: ReceiveChannel<NavigationEvent> = _navigationEvents

    private val isSearchMode by savedStateHandle.stateFlow(initialValue = false)

    private val screen = MutableStateFlow<Screen>(Screen.WordsOfTheDay)

    override val toolbarState = combine(isSearchMode, screen) { isSearchMode, screen ->
        ToolbarState(isSearchMode, screen)
    }

    override val searchText by savedStateHandle.stateFlow(initialValue = EMPTY)

    override val searchSuggestions: Flow<List<Suggestion>> = searchText
        .flatMapLatest { searchText ->
            if (searchText.isBlank()) {
                flowOf(ContentState.Loaded(emptyList()))
            } else {
                getSearchSuggestions(searchText)
            }
        }
        .transform { contentState ->
            if (contentState is ContentState.Loaded) {
                emit(contentState.content)
            }
        }
        .cacheIn(viewModelScope)

    init {
        isSearchMode.onEach { isSearchMode ->
            if (!isSearchMode) {
                clearSearchText()
            }
        }.launchIn(viewModelScope)
    }

    override fun navigate(navDirections: NavDirections) {
        _navigationEvents.offer(NavigationEvent.Navigate(navDirections))
    }

    override fun shareLink(link: String) {
        _navigationEvents.offer(NavigationEvent.ShareLink(link))
    }

    override fun openWebpage(url: Uri) {
        _navigationEvents.offer(NavigationEvent.OpenWebpage(url))
    }

    override fun openMailClient(address: String) {
        _navigationEvents.offer(NavigationEvent.OpenMailClient(address))
    }

    override fun onSearchBackPressed() = setSearchMode(false)
    override fun onSearchShadeClicked() = setSearchMode(false)
    override fun onSearchBackIconClicked() = setSearchMode(false)
    override fun onSearchIconClicked() = setSearchMode(true)
    override fun onSearchFieldFocused() = setSearchMode(true)

    override fun onSearchTextChanged(text: String) {
        searchText.value = text
    }

    override fun onClearSearchClicked() = clearSearchText()

    private fun setSearchMode(isSearchMode: Boolean) {
        this.isSearchMode.value = isSearchMode
    }

    private fun clearSearchText() {
        searchText.value = EMPTY
    }

    override fun onRandomWordIconClicked() {
        navigate(NavGraphDirections.toRandomWordsFragment())
    }

    override fun onDefineWordClicked() {
        openWebpage(DEFINE_WORD_URL)
    }

    override fun onAboutItemClicked() {
        navigate(NavGraphDirections.toAboutDialog())
    }

    override fun onNavigateBackIconClicked() {
        navigate(NavGraphDirections.popUpToWordsOfTheDayFragment())
    }

    override fun onSuggestionClicked(suggestion: Suggestion) {
        openDefinition(suggestion.term)
    }

    override fun onSearchActionClicked() {
        if (searchText.value.isNotBlank()) {
            openDefinition(searchText.value)
        }
    }

    private fun openDefinition(term: String) {
        navigate(
            NavGraphDirections.toDefinitionsFragment(TermKey(term))
        )
        setSearchMode(false)
    }

    override fun setScreen(screen: Screen) {
        this.screen.value = screen
    }

}