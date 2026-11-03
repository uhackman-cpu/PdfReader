package com.uhackman.pdfreader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HighlightAnchorResolverTest {
    @Test
    fun restoresAnUnchangedGlyphRange() {
        val page = page("alpha target omega")
        val saved = highlightFor(page, 6, 12)

        val resolved = HighlightAnchorResolver.resolveInDocument(listOf(page), saved)

        assertEquals(0, resolved?.pageIndex)
        assertEquals(6..11, resolved?.glyphRange)
    }

    @Test
    fun findsTheSameTextAfterEarlierContentWasInserted() {
        val original = page("alpha target omega")
        val saved = highlightFor(original, 6, 12)
        val edited = page("prefix alpha target omega")

        val resolved = HighlightAnchorResolver.resolveInDocument(listOf(edited), saved)

        assertEquals(0, resolved?.pageIndex)
        assertEquals(13..18, resolved?.glyphRange)
    }

    @Test
    fun surroundingTextDisambiguatesRepeatedPhrases() {
        val original = page("alpha target omega")
        val saved = highlightFor(original, 6, 12)
        val edited = page("wrong target wrong; alpha target omega")

        val resolved = HighlightAnchorResolver.resolveInDocument(listOf(edited), saved)

        assertEquals(0, resolved?.pageIndex)
        assertEquals(26..31, resolved?.glyphRange)
    }

    @Test
    fun canFollowTheTextToAnotherPage() {
        val original = page("alpha target omega")
        val saved = highlightFor(original, 6, 12)
        val pages = listOf(page("first page"), page("prefix alpha target omega"))

        val resolved = HighlightAnchorResolver.resolveInDocument(pages, saved)

        assertEquals(1, resolved?.pageIndex)
        assertEquals(13..18, resolved?.glyphRange)
    }

    @Test
    fun returnsNullWhenTheSelectedTextNoLongerExists() {
        val original = page("alpha target omega")
        val saved = highlightFor(original, 6, 12)

        assertNull(
            HighlightAnchorResolver.resolveInDocument(
                listOf(page("completely different content")),
                saved
            )
        )
    }

    private fun highlightFor(page: PageText, start: Int, endExclusive: Int): Highlight {
        val startGlyph = page.glyphRangeForChars(start, endExclusive)?.first
            ?: error("Test range must map to glyphs")
        val endGlyph = page.glyphRangeForChars(start, endExclusive)?.last
            ?: error("Test range must map to glyphs")
        return Highlight(
            id = 1L,
            pageIndex = 0,
            startGlyph = startGlyph,
            endGlyph = endGlyph,
            text = page.text.substring(start, endExclusive),
            color = 0xFFFFFF00.toInt(),
            note = "",
            createdAt = 1L,
            startChar = start,
            endChar = endExclusive,
            prefixText = page.text.substring(0, start),
            suffixText = page.text.substring(endExclusive)
        )
    }

    private fun page(text: String): PageText {
        val glyphs = text.indices.map { index ->
            Glyph(
                left = index / text.length.coerceAtLeast(1).toFloat(),
                top = 0f,
                right = (index + 1) / text.length.coerceAtLeast(1).toFloat(),
                bottom = 0.1f,
                startChar = index,
                endChar = index + 1
            )
        }
        return PageText(text, glyphs, 612f, 792f)
    }
}
