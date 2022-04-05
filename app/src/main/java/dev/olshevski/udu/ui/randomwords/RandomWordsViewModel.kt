package dev.olshevski.udu.ui.randomwords

import dev.olshevski.udu.domain.GetRandomWords
import dev.olshevski.udu.domain.VoteForDefinition
import dev.olshevski.udu.ui.base.pageddefinitions.PagedDefinitionsViewModel
import dev.olshevski.udu.ui.main.MainState
import dev.olshevski.udu.ui.main.mainActivity
import dev.olshevski.udu.ui.main.mainState
import dev.olshevski.udu.ui.main.model.Screen
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

fun RandomWordsFragment.randomWordsViewModel() = viewModel<RandomWordsViewModel> {
    parametersOf(mainActivity.mainState().value)
}

class RandomWordsViewModel(
    private val mainState: MainState,
    getRandomWords: GetRandomWords,
    voteForDefinition: VoteForDefinition
) : PagedDefinitionsViewModel(mainState, getRandomWords, voteForDefinition) {

    fun onStart() {
        mainState.setScreen(Screen.RandomWords)
    }

}