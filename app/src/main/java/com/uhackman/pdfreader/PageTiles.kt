package com.uhackman.pdfreader

import kotlin.math.ceil
import kotlin.math.floor

data class PageTileBounds(
    val leftPx: Int,
    val topPx: Int,
    val rightPx: Int,
    val bottomPx: Int
) {
    val widthPx: Int get() = rightPx - leftPx
    val heightPx: Int get() = bottomPx - topPx
}

/** Normalized page area currently visible through a zoomed page view. */
data class PageTileRegion(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    init {
        require(left.isFinite() && top.isFinite() && right.isFinite() && bottom.isFinite())
        require(left <= right && top <= bottom)
    }

    companion object {
        val FULL_PAGE = PageTileRegion(0f, 0f, 1f, 1f)
    }
}

data class PageRasterSize(val widthPx: Int, val heightPx: Int)

data class PageTile(
    val bounds: PageTileBounds,
    val pageWidth: Int,
    val pageHeight: Int,
    val bitmap: android.graphics.Bitmap,
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

    /** Uses the already-rotated/cropped dimensions supplied by PDFRenderer. */
    fun rasterSize(widthPoints: Float, heightPoints: Float, dpi: Float): PageRasterSize {
        require(widthPoints.isFinite() && heightPoints.isFinite() && dpi.isFinite())
        require(widthPoints > 0f && heightPoints > 0f && dpi > 0f)
        val scale = dpi / 72f
        return PageRasterSize(
            widthPx = floor(widthPoints * scale).toInt().coerceAtLeast(1),
            heightPx = floor(heightPoints * scale).toInt().coerceAtLeast(1)
        )
    }

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

    /**
     * Plans only tiles intersecting the normalized viewport plus a small
     * prefetch margin. Coordinates are clamped to the rendered page.
     */
    fun planVisible(
        width: Int,
        height: Int,
        region: PageTileRegion,
        tileSize: Int = DEFAULT_TILE_SIZE,
        prefetchTiles: Int = 1
    ): List<PageTileBounds> {
        require(width > 0 && height > 0) { "Page dimensions must be positive" }
        require(tileSize > 0) { "Tile size must be positive" }
        require(prefetchTiles >= 0) { "Prefetch tile count cannot be negative" }

        val left = region.left.coerceIn(0f, 1f)
        val top = region.top.coerceIn(0f, 1f)
        val right = region.right.coerceIn(0f, 1f)
        val bottom = region.bottom.coerceIn(0f, 1f)
        if (right <= left || bottom <= top) return emptyList()

        val firstX = floor(left * width).toInt().coerceIn(0, width - 1)
        val firstY = floor(top * height).toInt().coerceIn(0, height - 1)
        val afterX = ceil(right * width).toInt().coerceIn(1, width)
        val afterY = ceil(bottom * height).toInt().coerceIn(1, height)
        val lastX = (afterX - 1).coerceIn(0, width - 1)
        val lastY = (afterY - 1).coerceIn(0, height - 1)

        val maxColumn = (width - 1) / tileSize
        val maxRow = (height - 1) / tileSize
        val firstColumn = (firstX / tileSize - prefetchTiles).coerceAtLeast(0)
        val lastColumn = (lastX / tileSize + prefetchTiles).coerceAtMost(maxColumn)
        val firstRow = (firstY / tileSize - prefetchTiles).coerceAtLeast(0)
        val lastRow = (lastY / tileSize + prefetchTiles).coerceAtMost(maxRow)

        val result = ArrayList<PageTileBounds>()
        for (row in firstRow..lastRow) {
            val tileTop = row * tileSize
            val tileBottom = minOf(tileTop + tileSize, height)
            for (column in firstColumn..lastColumn) {
                val tileLeft = column * tileSize
                val tileRight = minOf(tileLeft + tileSize, width)
                result.add(PageTileBounds(tileLeft, tileTop, tileRight, tileBottom))
            }
        }
        return result
    }
}
