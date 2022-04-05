package dev.olshevski.udu.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.olshevski.udu.data.paging.model.PageRange
import dev.olshevski.udu.data.paging.model.intersect
import dev.olshevski.udu.data.paging.model.shift

abstract class PositionalPagingSource<ContentT : Any> : PagingSource<Int, ContentT>() {

    final override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContentT> {
        val totalCount = getTotalCount()
        val range = calculateRange(params, totalCount)
        val page = loadRange(range)
        return LoadResult.Page(
            data = page,
            prevKey = if (range.start == 0) null else range.start,
            nextKey = if (range.end == totalCount) null else range.end,
            itemsBefore = range.start
        )
    }

    private fun calculateRange(params: LoadParams<Int>, totalCount: Int): PageRange {
        val listRange = PageRange(0, totalCount)
        val range = when (params) {
            is LoadParams.Refresh -> {
                // treat key as the central position
                val start = (params.key ?: 0) - params.loadSize / 2
                val end = start + params.loadSize
                val range = PageRange(start, end)

                // but if we are at the start or the end of the list, shift range accordingly
                when {
                    range.start < 0 -> range.shift(-range.start)
                    range.end > totalCount -> range.shift(totalCount - range.end)
                    else -> range
                }
            }
            is LoadParams.Append -> {
                // key is the start position
                val start = params.key
                val end = start + params.loadSize
                PageRange(start, end)
            }
            is LoadParams.Prepend -> {
                // key is the end position
                val end = params.key
                val start = end - params.loadSize
                PageRange(start, end)
            }
        } intersect listRange
        if (range == null) {
            error("calculated range is outside of listRange ($listRange)")
        } else {
            return range
        }
    }

    final override fun getRefreshKey(state: PagingState<Int, ContentT>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            /*
             During the first call to getRefreshKey there is some weird first empty page
             (Page(data=[], prevKey=null, nextKey=null, itemsBefore=-2147483648,
             itemsAfter=-2147483648)).

             It must be some bug in the version 3.0.0-alpha05 of Paging library. For now just ignore
             this page and find next page with actual data.
             */
            val itemsBefore = state.pages.find { it.data.isNotEmpty() }?.itemsBefore ?: 0
            anchorPosition + itemsBefore
        }

    abstract suspend fun getTotalCount(): Int

    abstract suspend fun loadRange(pageRange: PageRange): List<ContentT>

}