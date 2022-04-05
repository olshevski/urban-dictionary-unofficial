package dev.olshevski.udu.ui

import androidx.lifecycle.SavedStateHandle
import dev.olshevski.udu.ui.about.AboutViewModel
import dev.olshevski.udu.ui.definitions.DefinitionsFragmentArgs
import dev.olshevski.udu.ui.definitions.DefinitionsViewModel
import dev.olshevski.udu.ui.main.MainState
import dev.olshevski.udu.ui.main.MainViewModel
import dev.olshevski.udu.ui.randomwords.RandomWordsViewModel
import dev.olshevski.udu.ui.wordsoftheday.WordsOfTheDayViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (savedStateHandle: SavedStateHandle) ->
        MainViewModel(savedStateHandle, get())
    }
    viewModel { (mainState: MainState) ->
        WordsOfTheDayViewModel(mainState, get(), get())
    }
    viewModel { (mainState: MainState) ->
        RandomWordsViewModel(mainState, get(), get())
    }
    viewModel { (definitionFragmentArgs: DefinitionsFragmentArgs, mainState: MainState) ->
        DefinitionsViewModel(definitionFragmentArgs, mainState, get(), get())
    }
    viewModel { (mainState: MainState) ->
        AboutViewModel(mainState)
    }
}