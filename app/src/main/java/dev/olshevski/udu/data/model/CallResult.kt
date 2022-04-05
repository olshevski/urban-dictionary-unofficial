package dev.olshevski.udu.data.model

import dev.olshevski.udu.domain.base.model.ContentState

sealed class CallResult<T> {
    class Success<T>(val data: T) : CallResult<T>()
    class Error<T>(val exception: Exception) : CallResult<T>()
}

fun <T> CallResult<T>.toContentState() = when (this) {
    is CallResult.Success -> ContentState.Loaded(this.data)
    is CallResult.Error -> ContentState.Failed
}