package dev.olshevski.udu.ui.wordsoftheday

import dev.olshevski.udu.domain.GetWordsOfTheDay
import dev.olshevski.udu.domain.VoteForDefinition
import dev.olshevski.udu.ui.base.pageddefinitions.PagedDefinitionsViewModel
import dev.olshevski.udu.ui.main.MainState
import dev.olshevski.udu.ui.main.mainActivity
import dev.olshevski.udu.ui.main.mainState
import dev.olshevski.udu.ui.main.model.Screen
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

fun WordsOfTheDayFragment.wordsOfTheDayViewModel() = viewModel<WordsOfTheDayViewModel> {
    parametersOf(mainActivity.mainState().value)
}

class WordsOfTheDayViewModel(
    private val mainState: MainState,
    getWordsOfTheDay: GetWordsOfTheDay,
    voteForDefinition: VoteForDefinition
) : PagedDefinitionsViewModel(mainState, getWordsOfTheDay, voteForDefinition) {

    fun onStart() {
        mainState.setScreen(Screen.WordsOfTheDay)
    }

}