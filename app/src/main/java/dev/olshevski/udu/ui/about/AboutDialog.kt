package dev.olshevski.udu.ui.about

import android.app.Dialog
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.olshevski.udu.BuildConfig
import dev.olshevski.udu.R
import dev.olshevski.udu.ui.common.withClickableLinks
import kotlinx.android.synthetic.main.dialog_about.view.*

class AboutDialog : AppCompatDialogFragment() {

    private val viewModel by aboutViewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_about, null)
        view.version.text = BuildConfig.VERSION_NAME
        view.builtOn.text = BuildConfig.BUILD_DATE
        view.message.apply {
            text = getString(R.string.dialog_about__message)
                .withClickableLinks(viewModel::onLinkClicked)
            this.movementMethod = LinkMovementMethod.getInstance()
        }
        view.mail.setOnClickListener {
            viewModel.onMailClicked()
        }
        return MaterialAlertDialogBuilder(requireContext()).setView(view).create()
    }

}