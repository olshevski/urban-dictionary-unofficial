@file:Suppress("ReplaceRangeStartEndInclusiveWithFirstLast")

package dev.olshevski.udu.ui.common

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import java.util.regex.Pattern

/**
 * Pattern for definition links - words/phrases inside square brackets.
 */
private val LINK_PATTERN = Pattern.compile("\\[[^\\]]+\\]")

/**
 * Converts all definition links (words/phrases inside square brackets) into [ClickableSpan]s.
 */
fun String.withClickableLinks(linkClickedListener: (String) -> Unit): SpannableString {
    val linkRanges = findAllLinkRanges()
    val (stringWithoutLinkBrackets, shiftedRanges) = removeLinkBrackets(linkRanges)
    return stringWithoutLinkBrackets.addClickableSpans(shiftedRanges, linkClickedListener)
}

/**
 * ALl link ranges (including brackets).
 */
private fun String.findAllLinkRanges(): List<IntRange> {
    val matcher = LINK_PATTERN.matcher(this)
    val ranges = mutableListOf<IntRange>()
    while (matcher.find()) {
        ranges.add(IntRange(matcher.start(), matcher.end()))
    }
    return ranges
}

/**
 * Returns the string without square brackets and the modified list of all link ranges.
 */
private fun String.removeLinkBrackets(linkRanges: List<IntRange>): Pair<String, List<IntRange>> =
    if (linkRanges.isEmpty()) {
        this to linkRanges
    } else {
        val stringBuilder = StringBuilder(this)
        val shiftedRanges = ArrayList<IntRange>(linkRanges.size)
        var indexShifter = 0
        linkRanges.forEach { linkRange ->
            val shiftedRange =
                IntRange(linkRange.start - indexShifter, linkRange.endInclusive - indexShifter - 2)
            shiftedRanges.add(shiftedRange)
            stringBuilder.deleteCharAt(shiftedRange.start)
            stringBuilder.deleteCharAt(shiftedRange.endInclusive)
            indexShifter += 2
        }
        stringBuilder.toString() to shiftedRanges
    }

private fun String.addClickableSpans(
    linkRanges: List<IntRange>,
    linkClickedListener: (String) -> Unit
) = SpannableString(this).apply {
    linkRanges.forEach { linkRange ->
        val linkText = this@addClickableSpans.substring(linkRange.start, linkRange.endInclusive)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                linkClickedListener.invoke(linkText)
            }
        }
        setSpan(
            clickableSpan,
            linkRange.start,
            linkRange.endInclusive,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
        // TODO make links highlighted when clicked
    }
}
