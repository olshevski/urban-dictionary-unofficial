package dev.olshevski.udu.ui.wordsoftheday

import android.os.Bundle
import android.view.View
import dev.olshevski.udu.ui.base.pageddefinitions.PagedDefinitionsFragment
import dev.olshevski.udu.util.addRotationAgnosticLifecycleObserver
import dev.olshevski.udu.util.onStart
import dev.olshevski.udu.util.viewLifecycle

class WordsOfTheDayFragment : PagedDefinitionsFragment() {

    override val viewModel by wordsOfTheDayViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addRotationAgnosticLifecycleObserver(viewLifecycle,
            onStart {
                viewModel.onStart()
            }
        )
    }

}