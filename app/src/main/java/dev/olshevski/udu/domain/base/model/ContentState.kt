package dev.olshevski.udu.domain.base.model

sealed class ContentState<out T> {
    object Loading : ContentState<Nothing>()
    data class Loaded<T>(val content: T) : ContentState<T>()
    object Failed : ContentState<Nothing>()
}

fun <T, R> ContentState<T>.map(contentMapper: (T) -> R): ContentState<R> = when (this) {
    ContentState.Loading -> ContentState.Loading
    is ContentState.Loaded -> ContentState.Loaded(contentMapper(this.content))
    ContentState.Failed -> ContentState.Failed
}