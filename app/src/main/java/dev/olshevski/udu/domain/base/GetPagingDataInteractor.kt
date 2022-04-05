package dev.olshevski.udu.domain.base

import androidx.paging.Pager

abstract class GetPagingDataInteractor<ContentT : Any> {

    operator fun invoke() = getPager().flow

    abstract fun getPager(): Pager<Int, ContentT>

}