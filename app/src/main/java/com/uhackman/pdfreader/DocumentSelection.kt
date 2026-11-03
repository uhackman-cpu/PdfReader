package com.uhackman.pdfreader

data class SelectionEndpoint(val pageIndex: Int, val startGlyph: Int, val endGlyph: Int) {
    init {
        require(pageIndex >= 0)
        require(startGlyph >= 0)
        require(endGlyph >= startGlyph)
    }
}

data class PageSelectionRange(
    val pageIndex: Int,
    val startGlyph: Int,
    val endGlyph: Int,
    val startChar: Int,
    val endChar: Int,
    val selectedText: String,
    val prefixText: String,
    val suffixText: String
)

data class DocumentSelection(
    val anchor: SelectionEndpoint,
    val focus: SelectionEndpoint,
    val ranges: List<PageSelectionRange>,
    val selectedText: String
)

data class SelectionGestureEvent(
    val anchorStartGlyph: Int,
    val anchorEndGlyph: Int,
    val rawX: Float,
    val rawY: Float,
    val wordMode: Boolean,
    val started: Boolean = false,
    val finished: Boolean = false,
    val cancelled: Boolean = false
)

/** Builds ordered page-local fragments while retaining no whole-document glyph map. */
object DocumentSelectionBuilder {
    fun rangeForPage(
        anchor: SelectionEndpoint,
        focus: SelectionEndpoint,
        pageIndex: Int,
        glyphCount: Int
    ): IntRange? {
        if (glyphCount <= 0) return null
        val first = earlier(anchor, focus)
        val last = later(anchor, focus)
        if (pageIndex !in first.pageIndex..last.pageIndex) return null
        val start = if (pageIndex == first.pageIndex) first.startGlyph else 0
        val end = if (pageIndex == last.pageIndex) last.endGlyph else glyphCount - 1
        if (start !in 0 until glyphCount || end !in 0 until glyphCount || end < start) return null
        return start..end
    }

    fun build(
        anchor: SelectionEndpoint,
        focus: SelectionEndpoint,
        pageTexts: Map<Int, PageText>,
        pageCount: Int
    ): DocumentSelection? {
        val builder = streaming(anchor, focus, pageCount)
        for (index in builder.firstPageIndex..builder.lastPageIndex) {
            builder.accept(index, pageTexts[index] ?: return null)
        }
        return builder.result()
    }

    fun streaming(anchor: SelectionEndpoint, focus: SelectionEndpoint, pageCount: Int) =
        StreamingBuilder(anchor, focus, pageCount)

    class StreamingBuilder internal constructor(
        val anchor: SelectionEndpoint,
        val focus: SelectionEndpoint,
        pageCount: Int
    ) {
        private val first = earlier(anchor, focus)
        private val last = later(anchor, focus)
        val firstPageIndex = first.pageIndex
        val lastPageIndex = last.pageIndex
        private val ranges = mutableListOf<PageSelectionRange>()

        init {
            require(pageCount > 0)
            require(firstPageIndex in 0 until pageCount)
            require(lastPageIndex in firstPageIndex until pageCount)
        }

        fun accept(pageIndex: Int, page: PageText) {
            if (pageIndex !in firstPageIndex..lastPageIndex) return
            val range = rangeForPage(anchor, focus, pageIndex, page.glyphs.size) ?: return
            val firstGlyph = page.glyphs[range.first]
            val lastGlyph = page.glyphs[range.last]
            val startChar = firstGlyph.startChar.coerceIn(0, page.text.length)
            val endChar = lastGlyph.endChar.coerceIn(startChar, page.text.length)
            if (endChar <= startChar) return
            val fragment = page.text.substring(startChar, endChar).trim()
            if (fragment.isEmpty()) return
            ranges.add(
                PageSelectionRange(
                    pageIndex, range.first, range.last, startChar, endChar, fragment,
                    page.text.substring(maxOf(0, startChar - 48), startChar),
                    page.text.substring(endChar, minOf(page.text.length, endChar + 48))
                )
            )
        }

        fun result(): DocumentSelection? {
            if (ranges.isEmpty()) return null
            val ordered = ranges.sortedBy { it.pageIndex }
            return DocumentSelection(
                anchor, focus, ordered,
                ordered.joinToString("\n") { it.selectedText }.trim()
            )
        }
    }

    private fun earlier(a: SelectionEndpoint, b: SelectionEndpoint) =
        if (compare(a, b) <= 0) a else b

    private fun later(a: SelectionEndpoint, b: SelectionEndpoint) =
        if (compare(a, b) <= 0) b else a

    private fun compare(a: SelectionEndpoint, b: SelectionEndpoint): Int {
        if (a.pageIndex != b.pageIndex) return a.pageIndex.compareTo(b.pageIndex)
        return a.startGlyph.compareTo(b.startGlyph)
    }
}
