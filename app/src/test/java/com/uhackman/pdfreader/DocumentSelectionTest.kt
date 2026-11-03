package com.uhackman.pdfreader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DocumentSelectionTest {
    @Test
    fun forwardSelectionBuildsPerPageRangesAndCombinedText() {
        val pages = mapOf(0 to page("zero alpha"), 1 to page("middle page"), 2 to page("omega end"))
        val result = DocumentSelectionBuilder.build(
            SelectionEndpoint(0, 5, 9), SelectionEndpoint(2, 0, 4), pages, 3
        )
        assertEquals(listOf(0, 1, 2), result?.ranges?.map { it.pageIndex })
        assertEquals(listOf("alpha", "middle page", "omega"), result?.ranges?.map { it.selectedText })
        assertEquals("alpha\nmiddle page\nomega", result?.selectedText)
    }

    @Test
    fun reverseSelectionStillReturnsDocumentOrder() {
        val pages = mapOf(0 to page("zero alpha"), 1 to page("middle page"), 2 to page("omega end"))
        val result = DocumentSelectionBuilder.build(
            SelectionEndpoint(2, 0, 4), SelectionEndpoint(0, 5, 9), pages, 3
        )
        assertEquals("alpha\nmiddle page\nomega", result?.selectedText)
        assertEquals(listOf(0, 1, 2), result?.ranges?.map { it.pageIndex })
    }

    @Test
    fun samePageSelectionMergesEndpointRanges() {
        val result = DocumentSelectionBuilder.build(
            SelectionEndpoint(0, 2, 3), SelectionEndpoint(0, 6, 7),
            mapOf(0 to page("abcdefghij")), 1
        )
        assertEquals("cdefgh", result?.selectedText)
        assertEquals(2..7, result?.ranges?.single()?.let { it.startGlyph..it.endGlyph })
    }

    @Test
    fun sameStartPrefersTheWiderEndpointRange() {
        val result = DocumentSelectionBuilder.build(
            SelectionEndpoint(0, 3, 7),
            SelectionEndpoint(0, 3, 5),
            mapOf(0 to page("abcdefghij")),
            1
        )
        assertEquals("defgh", result?.selectedText)
        assertEquals(3..7, result?.ranges?.single()?.let { it.startGlyph..it.endGlyph })
    }

    @Test
    fun previewComputesPageLocalFragments() {
        val anchor = SelectionEndpoint(1, 4, 4)
        val focus = SelectionEndpoint(3, 2, 2)
        assertEquals(4..5, DocumentSelectionBuilder.rangeForPage(anchor, focus, 1, 6))
        assertEquals(0..3, DocumentSelectionBuilder.rangeForPage(anchor, focus, 2, 4))
        assertEquals(0..2, DocumentSelectionBuilder.rangeForPage(anchor, focus, 3, 5))
        assertNull(DocumentSelectionBuilder.rangeForPage(anchor, focus, 0, 5))
    }

    @Test
    fun streamingBuilderSkipsBlankPageFragments() {
        val builder = DocumentSelectionBuilder.streaming(
            SelectionEndpoint(0, 0, 0), SelectionEndpoint(2, 2, 2), 3
        )
        builder.accept(0, page("first"))
        builder.accept(1, page(""))
        builder.accept(2, page("end"))
        assertEquals("first\nend", builder.result()?.selectedText)
        assertEquals(listOf(0, 2), builder.result()?.ranges?.map { it.pageIndex })
    }

    private fun page(text: String): PageText {
        val glyphs = text.indices.map { index ->
            Glyph(
                index / text.length.coerceAtLeast(1).toFloat(), 0f,
                (index + 1) / text.length.coerceAtLeast(1).toFloat(), 0.1f,
                index, index + 1
            )
        }
        return PageText(text, glyphs, 612f, 792f)
    }
}
