@file:Suppress("unused")

package dev.olshevski.udu.util

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates

fun CombinedLoadStates.toShortString() =
    "source\t${source.toShortString()}\tmediator\t${mediator?.toShortString()}"

fun LoadStates.toShortString() =
    "P=${prepend.toShortString()}\tR=${refresh.toShortString()}\tA=${append.toShortString()}"

fun LoadState.toShortString() =
    when (this) {
        is LoadState.NotLoading -> "NL" + if (this.endOfPaginationReached) "(e)" else "   "
        is LoadState.Loading -> "L    "
        is LoadState.Error -> "E    "
    }