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
}
