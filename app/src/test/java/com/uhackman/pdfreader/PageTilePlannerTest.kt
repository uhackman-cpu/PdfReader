package com.uhackman.pdfreader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PageTilePlannerTest {
    @Test
    fun coversNonMultiplePageDimensionsWithoutGaps() {
        val tiles = PageTilePlanner.plan(1200, 900, 512)
        assertEquals(6, tiles.size)
        assertEquals(PageTileBounds(0, 0, 512, 512), tiles.first())
        assertEquals(PageTileBounds(1024, 512, 1200, 900), tiles.last())

        val coveredArea = tiles.sumOf { it.widthPx.toLong() * it.heightPx.toLong() }
        assertEquals(1_080_000L, coveredArea)
    }

    @Test
    fun everyTileIsWithinPageAndNoTilesOverlap() {
        val width = 1025
        val height = 777
        val tiles = PageTilePlanner.plan(width, height, 256)
        for (tile in tiles) {
            assertTrue(tile.leftPx >= 0 && tile.topPx >= 0)
            assertTrue(tile.rightPx <= width && tile.bottomPx <= height)
            assertTrue(tile.widthPx in 1..256)
            assertTrue(tile.heightPx in 1..256)
        }
        for (first in tiles.indices) {
            for (second in first + 1 until tiles.size) {
                val a = tiles[first]
                val b = tiles[second]
                val overlaps = a.leftPx < b.rightPx && b.leftPx < a.rightPx &&
                    a.topPx < b.bottomPx && b.topPx < a.bottomPx
                assertTrue("Tiles $first and $second overlap", !overlaps)
            }
        }
    }

    @Test
    fun visibleViewportRequestsOnlyIntersectingTilesWithoutPrefetch() {
        val region = PageTileRegion(0.5f, 0.5f, 0.75f, 0.75f)
        val tiles = PageTilePlanner.planVisible(2048, 2048, region, 512, prefetchTiles = 0)
        assertEquals(4, tiles.size)
        assertEquals(PageTileBounds(1024, 1024, 1536, 1536), tiles.first())
        assertEquals(PageTileBounds(1536, 1536, 2048, 2048), tiles.last())
    }

    @Test
    fun visibleViewportPrefetchesOneTileAndClampsAtPageEdges() {
        val region = PageTileRegion(0f, 0f, 0.25f, 0.25f)
        val tiles = PageTilePlanner.planVisible(2048, 2048, region, 512, prefetchTiles = 1)
        assertEquals(4, tiles.size)
        assertEquals(PageTileBounds(0, 0, 512, 512), tiles.first())
        assertEquals(PageTileBounds(512, 512, 1024, 1024), tiles.last())
    }

    @Test
    fun viewportOutsidePageProducesNoTiles() {
        val region = PageTileRegion(1.2f, 0.2f, 1.4f, 0.8f)
        assertTrue(PageTilePlanner.planVisible(1000, 1000, region).isEmpty())
    }

    @Test
    fun rasterSizeUsesDisplayedPageDimensionsAndDpi() {
        val size = PageTilePlanner.rasterSize(842f, 595f, 144f)
        assertEquals(PageRasterSize(1684, 1190), size)
    }

    @Test
    fun tileDpiStaysWithinTheTemporaryRasterBudget() {
        val dpi = RenderDpiPolicy.capForTiles(595f, 842f, 288f)
        val pixels = 595.0 * 842.0 * (dpi / 72.0) * (dpi / 72.0)
        assertTrue(pixels <= RenderDpiPolicy.MAX_TILED_BITMAP_PIXELS + 1.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsEmptyPage() {
        PageTilePlanner.plan(0, 100)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsInvalidTileSize() {
        PageTilePlanner.plan(100, 100, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsNegativePrefetch() {
        PageTilePlanner.planVisible(100, 100, PageTileRegion.FULL_PAGE, prefetchTiles = -1)
    }
}
