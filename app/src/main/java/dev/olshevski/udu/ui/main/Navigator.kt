package dev.olshevski.udu.ui.main

import android.net.Uri
import androidx.navigation.NavDirections

interface Navigator {
    fun navigate(navDirections: NavDirections)
    fun shareLink(link: String)
    fun openWebpage(url: Uri)
    fun openMailClient(address: String)
}