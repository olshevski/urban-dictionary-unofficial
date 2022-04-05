package dev.olshevski.udu.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import dev.olshevski.blueprint.ui.NavigationEvent
import dev.olshevski.udu.R
import dev.olshevski.udu.ui.main.model.Screen
import dev.olshevski.udu.ui.main.model.ToolbarState
import dev.olshevski.udu.util.closeKeyboard
import dev.olshevski.udu.util.getColorFromTheme
import dev.olshevski.udu.util.openKeyboard
import dev.olshevski.udu.util.runInStartedScope
import dev.olshevski.udu.util.unsafeLazy
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val vmContract by mainContract()

    private val navController by unsafeLazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
            .navController
    }

    private var searchBackPressedCallback: SearchBackPressedCallback? = null

    private val suggestionsAdapter = SuggestionsAdapter { suggestion ->
        vmContract.onSuggestionClicked(suggestion)
    }.apply {
        stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // https://developer.android.com/topic/performance/vitals/launch-time#solutions-3
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        search.apply {
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    vmContract.onSearchFieldFocused()
                    openKeyboard(search)
                } else {
                    closeKeyboard(search)
                }
            }
            doOnTextChanged { text, _, _, _ ->
                text?.let {
                    vmContract.onSearchTextChanged(text.toString())
                }
            }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    vmContract.onSearchActionClicked()
                    true
                } else {
                    false
                }
            }
        }
        search_shade.setOnClickListener {
            vmContract.onSearchShadeClicked()
        }
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.random_words -> vmContract.onRandomWordIconClicked()
                R.id.about -> vmContract.onAboutItemClicked()
                R.id.clear_search -> vmContract.onClearSearchClicked()
                R.id.define_new_word -> vmContract.onDefineWordClicked()
            }
            true
        }
        suggestions.adapter = suggestionsAdapter

        lifecycle.runInStartedScope {
            launch {
                for (navigationEvent in vmContract.navigationEvents) {
                    when (navigationEvent) {
                        is NavigationEvent.Navigate ->
                            navController.navigate(navigationEvent.navDirections)
                        is NavigationEvent.ShareLink -> shareLink(navigationEvent.link)
                        is NavigationEvent.OpenWebpage -> openWebpage(navigationEvent.url)
                        is NavigationEvent.OpenMailClient -> openMailClient(navigationEvent.address)
                    }
                }
            }
            vmContract.toolbarState.onEach { toolbarState ->
                updateToolbarNavigationButton(toolbarState)
                updateToolbarMenu(toolbarState)
                updateSearchShade(toolbarState)
                updateBackPressedDispatcher(toolbarState)
                updateSearchField(toolbarState)
                updateSearchFieldFocus(toolbarState)
            }.launchIn(this)
            vmContract.searchText.onEach { searchText ->
                // don't update text that is already entered
                if (search.text.toString() != searchText) {
                    search.setText(searchText)
                }
            }.launchIn(this)
            vmContract.searchSuggestions.onEach { searchSuggestions ->
                // reset scrolling position unless we are setting new list after rotation
                // (itemCount == 0) or submitting the same list in onStart after activity was in
                // background
                if (suggestionsAdapter.itemCount != 0 &&
                    suggestionsAdapter.suggestions != searchSuggestions
                ) {
                    suggestions.scrollToPosition(0)
                }
                suggestionsAdapter.suggestions = searchSuggestions
                searchSuggestions.isNotEmpty().let {
                    suggestions.isVisible = it
                    suggestions_separator.isVisible = it
                }
            }.launchIn(this)
        }
    }

    private fun updateToolbarNavigationButton(toolbarState: ToolbarState) {
        when {
            toolbarState.isSearchMode -> {
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
                toolbar.setNavigationOnClickListener { vmContract.onSearchBackIconClicked() }
            }
            toolbarState.screen is Screen.WordsOfTheDay -> {
                toolbar.setNavigationIcon(R.drawable.ic_search_24dp)
                toolbar.setNavigationOnClickListener { vmContract.onSearchIconClicked() }
            }
            else -> {
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
                toolbar.setNavigationOnClickListener { vmContract.onNavigateBackIconClicked() }
            }
        }
    }

    private fun updateToolbarMenu(toolbarState: ToolbarState) {
        toolbar.menu.clear()
        toolbar.inflateMenu(
            if (toolbarState.isSearchMode) {
                R.menu.menu_search
            } else {
                R.menu.menu_default
            }
        )
    }

    private fun updateSearchShade(toolbarState: ToolbarState) {
        if (toolbarState.isSearchMode) {
            search_shade.show()
        } else {
            search_shade.hide()
        }
    }

    private fun updateSearchField(toolbarState: ToolbarState) {
        val text: String? = when {
            toolbarState.isSearchMode || toolbarState.screen is Screen.WordsOfTheDay -> null
            toolbarState.screen is Screen.RandomWords -> getString(R.string.activity_main__random_words_title)
            toolbarState.screen is Screen.Definition -> toolbarState.screen.term
            else -> error("unsupported state: $this")
        }
        search.hint = text ?: getString(R.string.activity_main__search_hint)
        val colorAttr =
            if (text == null) android.R.attr.textColorHint else android.R.attr.textColorPrimary
        search.setHintTextColor(getColorFromTheme(colorAttr))
    }

    private fun updateSearchFieldFocus(toolbarState: ToolbarState) {
        if (toolbarState.isSearchMode) {
            search.requestFocus()
        } else {
            search.clearFocus()
        }
    }

    private fun updateBackPressedDispatcher(toolbarState: ToolbarState) {
        if (toolbarState.isSearchMode) {
            if (searchBackPressedCallback == null) {
                searchBackPressedCallback = SearchBackPressedCallback().also {
                    onBackPressedDispatcher.addCallback(this@MainActivity, it)
                }
            }
        } else {
            searchBackPressedCallback?.let {
                it.remove()
                searchBackPressedCallback = null
            }
        }
    }

    private inner class SearchBackPressedCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            vmContract.onSearchBackPressed()
        }
    }

    private fun shareLink(link: String) {
        val intent: Intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }
        startActivity(Intent.createChooser(intent, null))
    }

    private fun openWebpage(url: Uri) {
        val builder = CustomTabsIntent.Builder()
        builder.setStartAnimations(
            this,
            R.anim.nav_anim_bottom_enter,
            R.anim.nav_anim_bottom_exit
        )
        builder.setExitAnimations(
            this,
            R.anim.nav_anim_bottom_pop_enter,
            R.anim.nav_anim_bottom_pop_exit
        )
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, url)
    }

    private fun openMailClient(address: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$address"))
        val chooserTitle = getString(R.string.chooser_title_contact_developer)
        startActivity(Intent.createChooser(intent, chooserTitle))
    }

}

val Fragment.mainActivity get() = requireActivity() as MainActivity