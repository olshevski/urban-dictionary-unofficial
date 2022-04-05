package dev.olshevski.udu.domain.base

import dev.olshevski.udu.data.model.CallResult
import dev.olshevski.udu.data.model.toContentState
import dev.olshevski.udu.domain.base.model.ContentState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class GetContentStateInteractor<ParamT, ContentT : Any> {

    open operator fun invoke(param: ParamT): Flow<ContentState<ContentT>> = flow {
        emit(ContentState.Loading)
        emit(getContent(param).toContentState())
    }

    abstract suspend fun getContent(param: ParamT): CallResult<ContentT>

}