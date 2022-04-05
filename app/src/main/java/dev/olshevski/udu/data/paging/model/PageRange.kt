package dev.olshevski.udu.data.paging.model

import kotlin.math.max
import kotlin.math.min

/**
 * Range with inclusive [start] and exclusive [end]. Also, [end] is always greater or equal to
 * [start].
 */
data class PageRange(val start: Int, val end: Int) {
    init {
        require(start <= end) { "start should be less or equal to end" }
    }
}

/**
 * Calculates intersection of two ranges. Returns null if they do not intersect in at least one
 * point.
 */
infix fun PageRange.intersect(other: PageRange): PageRange? {
    val intersectedStart = max(this.start, other.start)
    val intersectedEnd = min(this.end, other.end)
    return if (intersectedStart > intersectedEnd) {
        null
    } else {
        PageRange(
            intersectedStart,
            intersectedEnd
        )
    }
}

fun PageRange.shift(value: Int) =
    PageRange(start + value, end + value)