package com.uhackman.pdfreader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PageTextCacheTest {
    private fun page(label: String): PageText = PageText(
        text = label,
        glyphs = listOf(Glyph(0f, 0f, 0.1f, 0.1f, 0, label.length)),
        widthPoints = 100f,
        heightPoints = 100f
    )

    @Test
    fun visiblePageLeasePreventsEvictionUntilRecycle() {
        val cache = PageTextCache(maxBytes = 400)
        val first = page("first")
        val second = page("second")

        assertTrue(cache.put(0, first))
        assertNotNull(cache.acquire(0))
        assertFalse(cache.put(1, second))
        assertTrue(cache.contains(0))
        assertFalse(cache.contains(1))

        cache.release(0)
        assertTrue(cache.put(1, second))
        assertFalse(cache.contains(0))
        assertTrue(cache.contains(1))
        assertTrue(cache.weightBytes() <= 400)
    }

    @Test
    fun acquireReleaseAndClearKeepCacheWithinBudget() {
        val cache = PageTextCache(maxBytes = 800)
        assertTrue(cache.put(0, page("alpha")))
        assertTrue(cache.put(1, page("beta")))

        assertEquals("alpha", cache.acquire(0)?.text)
        cache.release(0)
        cache.clear()

        assertEquals(0, cache.entryCount())
        assertEquals(0, cache.weightBytes())
        assertNull(cache.acquire(0))
    }
}
