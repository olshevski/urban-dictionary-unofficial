package dev.olshevski.udu.util

import android.content.Context
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment

/* assorted utils that don't have larger groups atm */

val Fragment.viewLifecycle get() = viewLifecycleOwner.lifecycle

fun String.singleLine(separator: String = " ") = lineSequence()
    .map { it.trim() }
    .joinToString(separator)

/**
 * Shortcut function for using [lazy] with [LazyThreadSafetyMode.NONE] flag.
 */
fun <T> unsafeLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

fun checkMainThread(lazyMessage: () -> Any = { "Not running on main thread" }) {
    check(Thread.currentThread() === Looper.getMainLooper().thread, lazyMessage)
}

fun Context.openKeyboard(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, 0)
}

fun Context.closeKeyboard(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

@ColorInt
fun Context.getColorFromTheme(@AttrRes attr: Int) = TypedValue().let {
    theme.resolveAttribute(attr, it, true)
    it.data
}