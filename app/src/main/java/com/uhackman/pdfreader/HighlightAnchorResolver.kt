package com.uhackman.pdfreader

import java.util.Locale
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

    private data class NormalizedText(
        val value: String,
        val sourceStarts: IntArray,
        val sourceEnds: IntArray
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

        val target = normalizeWithOffsets(highlight.text).value
        if (target.isEmpty()) return emptyList()
        val normalizedPage = normalizeWithOffsets(page.text)
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
        while (searchFrom <= normalizedPage.value.length - target.length) {
            val found = normalizedPage.value.indexOf(target, searchFrom)
            if (found < 0) break
            val startChar = normalizedPage.sourceStarts[found]
            val endChar = normalizedPage.sourceEnds[found + target.length - 1]
            page.glyphRangeForChars(startChar, endChar)?.let(ranges::add)
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
            val prefixScore = matchingSuffixLength(
                normalize(before),
                normalize(highlight.prefixText)
            )
            val suffixScore = matchingPrefixLength(
                normalize(after),
                normalize(highlight.suffixText)
            )
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

    private fun normalize(value: String): String = normalizeWithOffsets(value).value

    /**
     * Collapses all Unicode whitespace and case-folds text while retaining a
     * mapping from every normalized UTF-16 unit to its original source span.
     * That mapping lets a normalized match become the correct glyph range even
     * when line wrapping or whitespace changes between document opens.
     */
    private fun normalizeWithOffsets(value: String): NormalizedText {
        val normalized = StringBuilder()
        val starts = ArrayList<Int>()
        val ends = ArrayList<Int>()
        var pendingWhitespaceStart = -1
        var pendingWhitespaceEnd = -1
        var sourceIndex = 0

        while (sourceIndex < value.length) {
            val codePoint = Character.codePointAt(value, sourceIndex)
            val nextIndex = sourceIndex + Character.charCount(codePoint)
            if (Character.isWhitespace(codePoint) || Character.isSpaceChar(codePoint)) {
                if (normalized.isNotEmpty()) {
                    if (pendingWhitespaceStart < 0) pendingWhitespaceStart = sourceIndex
                    pendingWhitespaceEnd = nextIndex
                }
            } else {
                if (pendingWhitespaceStart >= 0) {
                    normalized.append(' ')
                    starts.add(pendingWhitespaceStart)
                    ends.add(pendingWhitespaceEnd)
                    pendingWhitespaceStart = -1
                    pendingWhitespaceEnd = -1
                }

                val folded = String(Character.toChars(codePoint)).lowercase(Locale.ROOT)
                for (unit in folded) {
                    normalized.append(unit)
                    starts.add(sourceIndex)
                    ends.add(nextIndex)
                }
            }
            sourceIndex = nextIndex
        }

        return NormalizedText(
            value = normalized.toString(),
            sourceStarts = starts.toIntArray(),
            sourceEnds = ends.toIntArray()
        )
    }
}
