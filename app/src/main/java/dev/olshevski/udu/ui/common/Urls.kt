package dev.olshevski.udu.ui.common

import android.net.Uri

val MAIN_PAGE_URL: Uri = Uri.parse("https://www.urbandictionary.com")

val DEFINE_WORD_URL: Uri = Uri.parse("https://www.urbandictionary.com/add.php")

fun defineWordUrlWithHint(wordHint: String): Uri =
    DEFINE_WORD_URL.buildUpon().appendQueryParameter("word", wordHint).build()