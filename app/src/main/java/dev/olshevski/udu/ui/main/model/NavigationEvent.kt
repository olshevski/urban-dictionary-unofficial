package dev.olshevski.blueprint.ui

import android.net.Uri
import androidx.navigation.NavDirections

sealed class NavigationEvent {
    class Navigate(val navDirections: NavDirections) : NavigationEvent()
    class ShareLink(val link: String) : NavigationEvent()
    class OpenWebpage(val url: Uri) : NavigationEvent()
    class OpenMailClient(val address: String) : NavigationEvent()
}