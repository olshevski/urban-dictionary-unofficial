package dev.olshevski.udu.ui.common

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dev.olshevski.udu.R
import dev.olshevski.udu.util.getColorFromTheme

fun SwipeRefreshLayout.applyStyle() = apply {
    setColorSchemeColors(context.getColorFromTheme(R.attr.colorAccent))
    setProgressBackgroundColorSchemeColor(context.getColorFromTheme(android.R.attr.colorBackground))
}