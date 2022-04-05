package dev.olshevski.udu.ui.main

import dev.olshevski.blueprint.ui.NavigationEvent
import dev.olshevski.udu.network.model.Suggestion
import dev.olshevski.udu.ui.main.model.ToolbarState
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface MainContract {
    val navigationEvents: ReceiveChannel<NavigationEvent>
    val toolbarState: Flow<ToolbarState>
    val searchSuggestions: Flow<List<Suggestion>>
    val searchText: MutableStateFlow<String>

    fun onNavigateBackIconClicked()
    fun onSearchIconClicked()
    fun onSearchBackIconClicked()
    fun onRandomWordIconClicked()
    fun onSearchBackPressed()
    fun onSearchShadeClicked()
    fun onAboutItemClicked()
    fun onSearchFieldFocused()
    fun onSearchTextChanged(text: String)
    fun onClearSearchClicked()
    fun onSuggestionClicked(suggestion: Suggestion)
    fun onSearchActionClicked()
    fun onDefineWordClicked()
}