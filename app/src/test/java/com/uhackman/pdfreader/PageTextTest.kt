package com.uhackman.pdfreader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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


    @Test
    fun longPressSelectionExpandsToAWordButKeepsPunctuationSeparate() {
        val page = pageFromText("Hello, world!")

        val word = TextSelection.wordRangeForGlyph(page, 1)
        val comma = TextSelection.wordRangeForGlyph(page, 5)

        assertEquals(0..4, word)
        assertEquals("Hello", page.textForGlyphRange(word!!.first, word.last))
        assertEquals(5..5, comma)
    }

    @Test
    fun wordSelectionKeepsInternalStraightAndCurlyApostrophes() {
        val straight = pageFromText("can't stop")
        val curly = pageFromText("can’t stop")

        val straightRange = TextSelection.wordRangeForGlyph(straight, 3)
        val curlyRange = TextSelection.wordRangeForGlyph(curly, 3)

        assertEquals("can't", straight.textForGlyphRange(straightRange!!.first, straightRange.last))
        assertEquals("can’t", curly.textForGlyphRange(curlyRange!!.first, curlyRange.last))
    }

    @Test
    fun wordSelectionIncludesCombiningMarksAndIndicSpacingMarks() {
        val text = "Cafe\u0301, दुनिया"
        val page = pageFromText(text)
        val accentGlyph = page.glyphs.indexOfFirst { it.startChar == text.indexOf('\u0301') }
        val hindiGlyph = page.glyphs.indexOfFirst { it.startChar == text.indexOf("द") }

        val accentRange = TextSelection.wordRangeForGlyph(page, accentGlyph)
        val hindiRange = TextSelection.wordRangeForGlyph(page, hindiGlyph)

        assertEquals("Cafe\u0301", page.textForGlyphRange(accentRange!!.first, accentRange.last))
        assertEquals("दुनिया", page.textForGlyphRange(hindiRange!!.first, hindiRange.last))
    }

    @Test
    fun spatialIndexMatchesFullScanForDenseAndSparseTouchPoints() {
        val glyphs = mutableListOf<Glyph>()
        for (row in 0 until 30) {
            for (column in 0 until 40) {
                val left = 0.01f + column * 0.024f
                val top = 0.01f + row * 0.032f
                val width = 0.006f + (column % 4) * 0.002f
                val height = 0.012f + (row % 3) * 0.003f
                glyphs.add(
                    Glyph(
                        left = left,
                        top = top,
                        right = (left + width).coerceAtMost(1f),
                        bottom = (top + height).coerceAtMost(1f),
                        startChar = glyphs.size,
                        endChar = glyphs.size + 1
                    )
                )
            }
        }

        val index = GlyphSpatialIndex(glyphs)
        val random = java.util.Random(17L)
        repeat(250) {
            val x = random.nextFloat() * 1.04f - 0.02f
            val y = random.nextFloat() * 1.04f - 0.02f
            assertEquals(bruteForceHitTest(glyphs, x, y), index.nearestGlyphIndex(x, y))
        }
    }

    @Test
    fun spatialIndexRejectsEmptyPagesAndOutOfBoundsTouches() {
        assertEquals(-1, GlyphSpatialIndex(emptyList()).nearestGlyphIndex(0.5f, 0.5f))
        assertEquals(-1, GlyphSpatialIndex(page.glyphs).nearestGlyphIndex(1.2f, 0.5f))
    }

    @Test
    fun gestureArbitrationLetsVerticalSwipesScrollAndHorizontalDragsSelect() {
        assertEquals(
            GestureIntent.VERTICAL_SCROLL,
            GestureArbitration.classify(3f, 18f, 8f)
        )
        assertEquals(
            GestureIntent.HORIZONTAL_SELECTION,
            GestureArbitration.classify(18f, 3f, 8f)
        )
        assertEquals(
            GestureIntent.UNDECIDED,
            GestureArbitration.classify(4f, 5f, 8f)
        )
    }

    @Test
    fun gestureArbitrationHandlesDiagonalMovementAndInvalidCoordinates() {
        assertEquals(
            GestureIntent.HORIZONTAL_SELECTION,
            GestureArbitration.classify(14f, 13f, 8f)
        )
        assertEquals(
            GestureIntent.UNDECIDED,
            GestureArbitration.classify(Float.NaN, 20f, 8f)
        )
    }

    @Test
    fun normalizedPageTransformRoundTripsThroughZoomAndPan() {
        val screen = PageCoordinateTransform.toScreen(
            normalizedX = 0.25f,
            normalizedY = 0.75f,
            left = 10f,
            top = 20f,
            right = 210f,
            bottom = 420f,
            zoom = 2f,
            offsetX = 3f,
            offsetY = -5f
        )
        assertEquals(123f, screen.x, 0.001f)
        assertEquals(635f, screen.y, 0.001f)

        val normalized = PageCoordinateTransform.toNormalized(
            screen.x, screen.y,
            10f, 20f, 210f, 420f,
            2f, 3f, -5f
        )

        assertEquals(0.25f, normalized?.x ?: -1f, 0.0001f)
        assertEquals(0.75f, normalized?.y ?: -1f, 0.0001f)
    }

    @Test
    fun displayedPageDimensionsMatchPdfRendererRotationRules() {
        assertEquals(CoordinatePoint(600f, 800f), displayedPageDimensions(600f, 800f, 0))
        assertEquals(CoordinatePoint(800f, 600f), displayedPageDimensions(600f, 800f, 90))
        assertEquals(CoordinatePoint(600f, 800f), displayedPageDimensions(600f, 800f, 180))
        assertEquals(CoordinatePoint(800f, 600f), displayedPageDimensions(600f, 800f, 270))
        assertEquals(CoordinatePoint(800f, 600f), displayedPageDimensions(600f, 800f, 450))
        assertEquals(CoordinatePoint(800f, 600f), displayedPageDimensions(600f, 800f, -90))
    }

    private fun pageFromText(text: String): PageText {
        val glyphs = mutableListOf<Glyph>()
        var charIndex = 0
        while (charIndex < text.length) {
            val codePoint = Character.codePointAt(text, charIndex)
            val end = charIndex + Character.charCount(codePoint)
            val index = glyphs.size
            val left = (index % 30) * 0.03f
            val top = (index / 30) * 0.03f
            glyphs.add(
                Glyph(left, top, left + 0.02f, top + 0.02f, charIndex, end)
            )
            charIndex = end
        }
        return PageText(text, glyphs, 612f, 792f)
    }

    private fun bruteForceHitTest(glyphs: List<Glyph>, x: Float, y: Float): Int {
        if (x < -0.02f || x > 1.02f || y < -0.02f || y > 1.02f) return -1
        var bestIndex = -1
        var bestScore = Float.POSITIVE_INFINITY
        glyphs.forEachIndexed { index, glyph ->
            val width = maxOf(glyph.right - glyph.left, 0.006f)
            val height = maxOf(glyph.bottom - glyph.top, 0.012f)
            val dx = (x - (glyph.left + glyph.right) / 2f) / width
            val dy = (y - (glyph.top + glyph.bottom) / 2f) / height
            val score = dx * dx + dy * dy
            if (score < bestScore ||
                (score == bestScore && (bestIndex < 0 || index < bestIndex))
            ) {
                bestScore = score
                bestIndex = index
            }
        }
        return if (bestIndex >= 0 && bestScore <= 36f) bestIndex else -1
    }



    @Test
    fun renderDpiPolicyKeepsNormalPagesSharpAndCapsLargeSheets() {
        assertEquals(288f, RenderDpiPolicy.cap(595f, 842f, 288f), 0.01f)

        val largePageDpi = RenderDpiPolicy.cap(2384f, 3370f, 288f)
        val renderedPixels = (2384.0 / 72.0 * largePageDpi) *
            (3370.0 / 72.0 * largePageDpi)

        assertTrue(largePageDpi < 120f)
        assertTrue(renderedPixels <= RenderDpiPolicy.MAX_BITMAP_PIXELS + 10.0)
    }

    @Test
    fun renderDpiPolicyFallsBackForInvalidPageDimensions() {
        assertEquals(180f, RenderDpiPolicy.cap(Float.NaN, 842f, 180f), 0.01f)
        assertEquals(
            RenderDpiPolicy.DEFAULT_DPI,
            RenderDpiPolicy.cap(595f, 842f, Float.NaN),
            0.01f
        )
    }

}
