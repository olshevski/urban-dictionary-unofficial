package dev.olshevski.udu.ui.base.pageddefinitions.model

sealed class PagingEvent {
    object Retry : PagingEvent()
    object Refresh : PagingEvent()
}