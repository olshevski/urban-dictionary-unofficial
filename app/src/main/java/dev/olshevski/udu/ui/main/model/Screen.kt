package dev.olshevski.udu.ui.main.model

sealed class Screen {
    object WordsOfTheDay : Screen()
    object RandomWords : Screen()
    data class Definition(val term: String?) : Screen()
}