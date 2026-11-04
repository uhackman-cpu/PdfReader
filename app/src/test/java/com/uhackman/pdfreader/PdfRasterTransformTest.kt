package com.uhackman.pdfreader

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PdfRasterTransformTest {
    @Test
    fun tileTransformOffsetsPageCoordinatesIntoTileLocalCoordinates() {
        assertArrayEquals(
            floatArrayOf(2f, 0f, -512f, 0f, 2f, -1024f, 0f, 0f, 1f),
            PdfRasterTransform.tileMatrixValues(2f, 512, 1024),
            0.0001f
        )
    }

    @Test
    fun acceptsPdfiumDimensionsMatchingDisplayedCropBox() {
        assertTrue(PdfRasterTransform.dimensionsCompatible(595f, 842f, 595.2f, 841.8f))
    }

    @Test
    fun rejectsDifferentCropOrUnrotatedPageAxes() {
        assertFalse(PdfRasterTransform.dimensionsCompatible(842f, 595f, 595f, 842f))
        assertFalse(PdfRasterTransform.dimensionsCompatible(612f, 792f, 595f, 842f))
    }

    @Test
    fun rejectsInvalidDimensionsAndTransformScale() {
        assertFalse(PdfRasterTransform.dimensionsCompatible(Float.NaN, 842f, 595f, 842f))
        assertFalse(PdfRasterTransform.dimensionsCompatible(595f, 842f, 0f, 842f))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsNonPositiveTileScale() {
        PdfRasterTransform.tileMatrixValues(0f, 0, 0)
    }
}
