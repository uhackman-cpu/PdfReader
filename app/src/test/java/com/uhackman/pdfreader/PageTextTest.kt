package com.uhackman.pdfreader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PageTextTest {
    private val page = PageText(
        text = "one two",
        glyphs = listOf(
            Glyph(0f, 0f, 0.1f, 0.1f, 0, 3),
            Glyph(0.2f, 0f, 0.3f, 0.1f, 4, 7)
        ),
        widthPoints = 100f,
        heightPoints = 100f
    )

    @Test
    fun mapsSearchCharactersBackToGlyphs() {
        assertEquals(1..1, page.glyphRangeForChars(4, 7))
        assertEquals(0..1, page.glyphRangeForChars(0, 7))
    }

    @Test
    fun extractsSelectedTextIncludingInterGlyphSpace() {
        assertEquals("one two", page.textForGlyphRange(0, 1))
        assertEquals("two", page.textForGlyphRange(1, 1))
    }

    @Test
    fun rejectsEmptyOrUnmappedRanges() {
        assertNull(page.glyphRangeForChars(4, 4))
        assertNull(page.glyphRangeForChars(20, 24))
    }
}
