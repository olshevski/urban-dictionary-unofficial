package dev.olshevski.udu.ui.about

import androidx.lifecycle.ViewModel
import dev.olshevski.udu.ui.common.MAIN_PAGE_URL
import dev.olshevski.udu.ui.main.MainState
import dev.olshevski.udu.ui.main.mainActivity
import dev.olshevski.udu.ui.main.mainState
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

fun AboutDialog.aboutViewModel() = viewModel<AboutViewModel> {
    parametersOf(mainActivity.mainState().value)
}

class AboutViewModel(private val mainState: MainState) : ViewModel() {

    companion object {
        private const val MAIL_ADDRESS = "tech@olshevski.dev"
        private const val URBAN_DICTIONARY_LINK = "urbandictionary.com"
    }

    fun onMailClicked() {
        mainState.openMailClient(MAIL_ADDRESS)
    }

    fun onLinkClicked(link: String) {
        when (link) {
            URBAN_DICTIONARY_LINK -> mainState.openWebpage(MAIN_PAGE_URL)
        }
    }
}