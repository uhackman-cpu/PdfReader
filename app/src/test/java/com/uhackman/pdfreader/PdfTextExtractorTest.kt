package com.uhackman.pdfreader

import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import org.junit.Assert.assertEquals
import org.junit.Test

class PdfTextExtractorTest {
    @Test
    fun cropBoxOriginDoesNotShiftNormalizedGlyphGeometryAcrossRotations() {
        for (rotation in listOf(0, 90, 180, 270)) {
            val reference = extractPage(
                cropLeft = 50f,
                cropBottom = 75f,
                rotation = rotation
            )
            val shifted = extractPage(
                cropLeft = 100f,
                cropBottom = 125f,
                rotation = rotation
            )
            val displayed = displayedPageDimensions(400f, 600f, rotation)

            assertEquals(displayed.x, shifted.widthPoints, 0.01f)
            assertEquals(displayed.y, shifted.heightPoints, 0.01f)
            assertEquals(reference.text, shifted.text)
            assertEquals(reference.glyphs.size, shifted.glyphs.size)

            reference.glyphs.zip(shifted.glyphs).forEachIndexed { index, pair ->
                val (expected, actual) = pair
                assertEquals("left at rotation $rotation glyph $index", expected.left, actual.left, 0.01f)
                assertEquals("top at rotation $rotation glyph $index", expected.top, actual.top, 0.01f)
                assertEquals("right at rotation $rotation glyph $index", expected.right, actual.right, 0.01f)
                assertEquals("bottom at rotation $rotation glyph $index", expected.bottom, actual.bottom, 0.01f)
            }
        }
    }

    private fun extractPage(
        cropLeft: Float,
        cropBottom: Float,
        rotation: Int
    ): PageText {
        PDDocument().use { document ->
            val page = PDPage(PDRectangle(600f, 800f))
            page.cropBox = PDRectangle(cropLeft, cropBottom, 400f, 600f)
            page.rotation = rotation
            document.addPage(page)

            PDPageContentStream(document, page).use { content ->
                content.beginText()
                content.setFont(PDType1Font.HELVETICA, 12f)
                content.newLineAtOffset(cropLeft + 50f, cropBottom + 525f)
                content.showText("CROP")
                content.endText()
            }

            return GlyphTextStripper().extractPage(document, 0)
        }
    }
}
