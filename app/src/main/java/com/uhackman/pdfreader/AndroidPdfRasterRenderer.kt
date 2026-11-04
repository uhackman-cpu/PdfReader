package com.uhackman.pdfreader

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer as AndroidPdfRenderer
import android.os.ParcelFileDescriptor
import java.io.Closeable
import java.io.File
import kotlin.math.abs

/**
 * Uses Android's platform PDFium renderer for page rasters while PDFBox remains
 * responsible for positioned text extraction and its fallback raster path.
 *
 * This gives image-heavy pages a second, independent decoder for embedded image
 * filters and transparency groups that may not be handled by PDFBox-Android.
 * A page is accepted only when PDFium's displayed dimensions agree with the
 * CropBox/rotation dimensions used by the text geometry model.
 */
class AndroidPdfRasterRenderer(file: File) : Closeable {
    private val descriptor = ParcelFileDescriptor.open(
        file,
        ParcelFileDescriptor.MODE_READ_ONLY
    )
    private val renderer = try {
        AndroidPdfRenderer(descriptor)
    } catch (error: Throwable) {
        try {
            descriptor.close()
        } catch (_: Exception) {
        }
        throw error
    }

    val pageCount: Int
        get() = renderer.pageCount

    @Synchronized
    fun renderPage(
        pageIndex: Int,
        expectedPage: PageDescriptor,
        dpi: Float
    ): Bitmap? {
        if (pageIndex !in 0 until renderer.pageCount || !dpi.isFinite() || dpi <= 0f) {
            return null
        }

        val page = renderer.openPage(pageIndex)
        var bitmap: Bitmap? = null
        try {
            if (!PdfRasterTransform.dimensionsCompatible(
                    page.width.toFloat(),
                    page.height.toFloat(),
                    expectedPage.widthPoints,
                    expectedPage.heightPoints
                )
            ) return null

            val size = PageTilePlanner.rasterSize(
                expectedPage.widthPoints,
                expectedPage.heightPoints,
                dpi
            )
            bitmap = Bitmap.createBitmap(size.widthPx, size.heightPx, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(Color.WHITE)
            val scale = dpi / 72f
            val matrix = Matrix().apply {
                setValues(PdfRasterTransform.tileMatrixValues(scale, 0, 0))
            }
            page.render(
                bitmap,
                Rect(0, 0, size.widthPx, size.heightPx),
                matrix,
                AndroidPdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
            )
            return bitmap.also { bitmap = null }
        } finally {
            bitmap?.let { if (!it.isRecycled) it.recycle() }
            page.close()
        }
    }

    @Synchronized
    fun renderPageTiles(
        pageIndex: Int,
        expectedPage: PageDescriptor,
        dpi: Float,
        bounds: List<PageTileBounds>
    ): List<PageTile>? {
        if (pageIndex !in 0 until renderer.pageCount || !dpi.isFinite() || dpi <= 0f) {
            return null
        }

        val page = renderer.openPage(pageIndex)
        val result = ArrayList<PageTile>(bounds.size)
        try {
            if (!PdfRasterTransform.dimensionsCompatible(
                    page.width.toFloat(),
                    page.height.toFloat(),
                    expectedPage.widthPoints,
                    expectedPage.heightPoints
                )
            ) return null

            val size = PageTilePlanner.rasterSize(
                expectedPage.widthPoints,
                expectedPage.heightPoints,
                dpi
            )
            val scale = dpi / 72f
            for (tileBounds in bounds.distinct()) {
                require(
                    tileBounds.leftPx >= 0 && tileBounds.topPx >= 0 &&
                        tileBounds.rightPx <= size.widthPx &&
                        tileBounds.bottomPx <= size.heightPx &&
                        tileBounds.widthPx > 0 && tileBounds.heightPx > 0
                ) { "Tile bounds are outside the rendered page: $tileBounds" }

                val bitmap = Bitmap.createBitmap(
                    tileBounds.widthPx,
                    tileBounds.heightPx,
                    Bitmap.Config.ARGB_8888
                )
                try {
                    bitmap.eraseColor(Color.WHITE)
                    val matrix = Matrix().apply {
                        setValues(
                            PdfRasterTransform.tileMatrixValues(
                                scale,
                                tileBounds.leftPx,
                                tileBounds.topPx
                            )
                        )
                    }
                    page.render(
                        bitmap,
                        Rect(0, 0, tileBounds.widthPx, tileBounds.heightPx),
                        matrix,
                        AndroidPdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                    )
                    result.add(
                        PageTile(
                            bounds = tileBounds,
                            pageWidth = size.widthPx,
                            pageHeight = size.heightPx,
                            bitmap = bitmap
                        )
                    )
                } catch (error: Throwable) {
                    if (!bitmap.isRecycled) bitmap.recycle()
                    throw error
                }
            }
            return result
        } catch (error: Throwable) {
            result.forEach { if (!it.bitmap.isRecycled) it.bitmap.recycle() }
            throw error
        } finally {
            page.close()
        }
    }

    @Synchronized
    override fun close() {
        renderer.close()
    }
}

/** Pure geometry shared by PDFium's page/tile rendering and its unit tests. */
internal object PdfRasterTransform {
    fun dimensionsCompatible(
        actualWidth: Float,
        actualHeight: Float,
        expectedWidth: Float,
        expectedHeight: Float,
        relativeTolerance: Float = 0.015f
    ): Boolean {
        val values = listOf(actualWidth, actualHeight, expectedWidth, expectedHeight, relativeTolerance)
        if (values.any { !it.isFinite() } || actualWidth <= 0f || actualHeight <= 0f ||
            expectedWidth <= 0f || expectedHeight <= 0f || relativeTolerance < 0f
        ) return false

        val widthError = abs(actualWidth - expectedWidth) / expectedWidth
        val heightError = abs(actualHeight - expectedHeight) / expectedHeight
        return widthError <= relativeTolerance && heightError <= relativeTolerance
    }

    /** Maps PDF page points into a tile bitmap whose origin is a page-pixel offset. */
    fun tileMatrixValues(scale: Float, tileLeftPx: Int, tileTopPx: Int): FloatArray {
        require(scale.isFinite() && scale > 0f)
        require(tileLeftPx >= 0 && tileTopPx >= 0)
        return floatArrayOf(
            scale, 0f, -tileLeftPx.toFloat(),
            0f, scale, -tileTopPx.toFloat(),
            0f, 0f, 1f
        )
    }
}
