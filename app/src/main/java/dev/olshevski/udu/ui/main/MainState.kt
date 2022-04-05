package dev.olshevski.udu.ui.main

import dev.olshevski.udu.ui.main.model.Screen

interface MainState : Navigator {
    fun setScreen(screen: Screen)
}