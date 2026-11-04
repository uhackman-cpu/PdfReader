package com.uhackman.pdfreader

import android.graphics.Bitmap

data class PageTileBounds(
    val leftPx: Int,
    val topPx: Int,
    val rightPx: Int,
    val bottomPx: Int
) {
    val widthPx: Int get() = rightPx - leftPx
    val heightPx: Int get() = bottomPx - topPx
}

data class PageTile(
    val bounds: PageTileBounds,
    val pageWidth: Int,
    val pageHeight: Int,
    val bitmap: Bitmap,
    val cacheKey: String = ""
) {
    val left: Float get() = bounds.leftPx.toFloat() / pageWidth
    val top: Float get() = bounds.topPx.toFloat() / pageHeight
    val right: Float get() = bounds.rightPx.toFloat() / pageWidth
    val bottom: Float get() = bounds.bottomPx.toFloat() / pageHeight
}

/** Deterministic row-major tile layout for a rendered page raster. */
object PageTilePlanner {
    const val DEFAULT_TILE_SIZE = 512

    fun plan(
        width: Int,
        height: Int,
        tileSize: Int = DEFAULT_TILE_SIZE
    ): List<PageTileBounds> {
        require(width > 0 && height > 0) { "Page dimensions must be positive" }
        require(tileSize > 0) { "Tile size must be positive" }

        val result = ArrayList<PageTileBounds>()
        var top = 0
        while (top < height) {
            val bottom = minOf(top + tileSize, height)
            var left = 0
            while (left < width) {
                val right = minOf(left + tileSize, width)
                result.add(PageTileBounds(left, top, right, bottom))
                left = right
            }
            top = bottom
        }
        return result
    }
}

/**
 * Splits a bounded high-resolution page raster into independently cacheable
 * bitmaps. The source raster is always recycled after the copies are made.
 */
object PageTileFactory {
    fun split(
        source: Bitmap,
        tileSize: Int = PageTilePlanner.DEFAULT_TILE_SIZE
    ): List<PageTile> {
        val pageWidth = source.width
        val pageHeight = source.height
        val bounds = PageTilePlanner.plan(pageWidth, pageHeight, tileSize)
        val result = ArrayList<PageTile>(bounds.size)
        try {
            val config = source.config ?: Bitmap.Config.ARGB_8888
            for (tile in bounds) {
                val isWholePage = tile.leftPx == 0 && tile.topPx == 0 &&
                    tile.rightPx == pageWidth && tile.bottomPx == pageHeight
                val bitmap = if (isWholePage) {
                    source.copy(config, false)
                        ?: throw IllegalStateException("Could not copy page raster")
                } else {
                    Bitmap.createBitmap(
                        source,
                        tile.leftPx,
                        tile.topPx,
                        tile.widthPx,
                        tile.heightPx
                    )
                }
                result.add(PageTile(tile, pageWidth, pageHeight, bitmap))
            }
            return result
        } catch (error: Throwable) {
            result.forEach { if (!it.bitmap.isRecycled) it.bitmap.recycle() }
            throw error
        } finally {
            if (!source.isRecycled) source.recycle()
        }
    }
}
