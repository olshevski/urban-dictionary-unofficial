package dev.olshevski.udu.ui.randomwords

import android.os.Bundle
import android.view.View
import dev.olshevski.udu.ui.base.pageddefinitions.PagedDefinitionsFragment
import dev.olshevski.udu.util.addRotationAgnosticLifecycleObserver
import dev.olshevski.udu.util.onStart
import dev.olshevski.udu.util.viewLifecycle

class RandomWordsFragment : PagedDefinitionsFragment() {

    override val viewModel by randomWordsViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addRotationAgnosticLifecycleObserver(viewLifecycle,
            onStart {
                viewModel.onStart()
            }
        )
    }

}