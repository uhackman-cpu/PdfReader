package com.uhackman.pdfreader

import kotlin.math.abs
import kotlin.math.min

data class ResolvedHighlightAnchor(
    val pageIndex: Int,
    val glyphRange: IntRange
)

/**
 * Re-resolves saved text highlights against freshly extracted page text.
 * Stored glyph indexes are a fast hint, not the only source of truth.
 */
object HighlightAnchorResolver {
    private data class Candidate(
        val pageIndex: Int,
        val range: IntRange,
        val contextScore: Int,
        val distance: Long
    )

    fun resolveInDocument(
        pages: List<PageText>,
        highlight: Highlight
    ): ResolvedHighlightAnchor? {
        if (pages.isEmpty() || normalize(highlight.text).isEmpty()) return null

        val best = pages.mapIndexedNotNull { pageIndex, page ->
            candidatesForPage(pageIndex, page, highlight).minWithOrNull(
                compareByDescending<Candidate> { it.contextScore }
                    .thenBy { it.distance }
            )
        }.minWithOrNull(
            compareByDescending<Candidate> { it.contextScore }
                .thenBy { abs(it.pageIndex - highlight.pageIndex) }
                .thenBy { it.distance }
        ) ?: return null

        return ResolvedHighlightAnchor(best.pageIndex, best.range)
    }

    private fun candidatesForPage(
        pageIndex: Int,
        page: PageText,
        highlight: Highlight
    ): List<Candidate> {
        if (page.glyphs.isEmpty()) return emptyList()

        val target = normalize(highlight.text)
        val ranges = linkedSetOf<IntRange>()

        val savedFirst = minOf(highlight.startGlyph, highlight.endGlyph)
        val savedLast = maxOf(highlight.startGlyph, highlight.endGlyph)
        if (savedFirst in page.glyphs.indices && savedLast in page.glyphs.indices) {
            val savedRange = savedFirst..savedLast
            if (normalize(page.textForGlyphRange(savedRange.first, savedRange.last)) == target) {
                ranges.add(savedRange)
            }
        }

        var searchFrom = 0
        while (searchFrom < page.text.length) {
            val found = page.text.indexOf(highlight.text, searchFrom, ignoreCase = true)
            if (found < 0) break
            page.glyphRangeForChars(found, found + highlight.text.length)?.let(ranges::add)
            searchFrom = found + 1
        }

        return ranges.mapNotNull { range ->
            val first = page.glyphs.getOrNull(range.first) ?: return@mapNotNull null
            val last = page.glyphs.getOrNull(range.last) ?: return@mapNotNull null
            val startChar = first.startChar
            val endChar = last.endChar
            if (startChar !in 0..page.text.length || endChar !in startChar..page.text.length) {
                return@mapNotNull null
            }

            val before = page.text.substring(0, startChar)
            val after = page.text.substring(endChar)
            val prefixScore = matchingSuffixLength(before, highlight.prefixText)
            val suffixScore = matchingPrefixLength(after, highlight.suffixText)
            val distance = if (highlight.startChar >= 0) {
                abs(startChar.toLong() - highlight.startChar.toLong())
            } else {
                abs(range.first.toLong() - highlight.startGlyph.toLong())
            }

            Candidate(
                pageIndex = pageIndex,
                range = range,
                contextScore = prefixScore + suffixScore,
                distance = distance
            )
        }
    }

    private fun matchingSuffixLength(actual: String, expected: String): Int {
        val limit = min(actual.length, expected.length)
        for (length in limit downTo 1) {
            if (actual.regionMatches(
                    actual.length - length,
                    expected,
                    expected.length - length,
                    length,
                    ignoreCase = true
                )
            ) return length
        }
        return 0
    }

    private fun matchingPrefixLength(actual: String, expected: String): Int {
        val limit = min(actual.length, expected.length)
        for (length in limit downTo 1) {
            if (actual.regionMatches(0, expected, 0, length, ignoreCase = true)) {
                return length
            }
        }
        return 0
    }

    private fun normalize(value: String): String =
        value.trim().replace(Regex("\\s+"), " ").lowercase()
}
