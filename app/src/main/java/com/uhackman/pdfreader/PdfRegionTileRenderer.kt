package com.uhackman.pdfreader

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.tom_roush.pdfbox.rendering.PDFRenderer

/**
 * Renders requested page regions directly into small bitmaps.
 *
 * PDFRenderer draws in the same crop-box and rotation-aware coordinate space
 * as renderImageWithDPI. The tile canvas is translated by the tile's pixel
 * origin before PDFRenderer applies its scale/rotation transform, so clipping
 * happens at the bitmap edge without allocating an intermediate full-page
 * high-resolution raster.
 */
object PdfRegionTileRenderer {
    fun renderPageTiles(
        renderer: PDFRenderer,
        pageIndex: Int,
        page: PageDescriptor,
        dpi: Float,
        bounds: List<PageTileBounds>
    ): List<PageTile> {
        require(pageIndex >= 0)
        val size = PageTilePlanner.rasterSize(page.widthPoints, page.heightPoints, dpi)
        val scale = dpi / 72f
        val result = ArrayList<PageTile>(bounds.size)

        try {
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
                    val canvas = Canvas(bitmap)
                    canvas.drawColor(Color.WHITE)
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.WHITE
                        style = Paint.Style.FILL
                    }
                    canvas.save()
                    try {
                        canvas.translate(-tileBounds.leftPx.toFloat(), -tileBounds.topPx.toFloat())
                        renderer.renderPageToGraphics(pageIndex, paint, canvas, scale, scale)
                    } finally {
                        canvas.restore()
                    }
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
        }
    }
}
