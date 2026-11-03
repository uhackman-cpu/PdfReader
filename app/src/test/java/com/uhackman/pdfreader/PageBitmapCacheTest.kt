package com.uhackman.pdfreader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PageBitmapCacheTest {
    @Test
    fun evictsLeastRecentlyUsedUnleasedEntry() {
        val evicted = mutableListOf<String>()
        val cache = BoundedLruCache<String, String>(
            maxWeight = 6,
            weightOf = { it.length },
            onEvicted = { evicted.add(it) }
        )

        cache.put("a", "aaa")
        cache.put("b", "bbb")
        cache.acquire("a")
        cache.release("a")
        cache.put("c", "ccc")

        assertTrue(cache.contains("a"))
        assertFalse(cache.contains("b"))
        assertTrue(cache.contains("c"))
        assertEquals(listOf("bbb"), evicted)
        assertTrue(cache.weight() <= 6)
    }

    @Test
    fun leasedPageCannotBeEvictedUntilItsViewReleasesIt() {
        val evicted = mutableListOf<String>()
        val cache = BoundedLruCache<String, String>(
            maxWeight = 4,
            weightOf = { it.length },
            onEvicted = { evicted.add(it) }
        )

        cache.put("visible", "123")
        assertEquals("123", cache.acquire("visible"))
        cache.put("prefetch", "456")

        assertTrue(cache.contains("visible"))
        assertFalse(cache.contains("prefetch"))
        assertTrue(evicted.contains("456"))

        cache.release("visible")
        cache.put("prefetch", "456")

        assertFalse(cache.contains("visible"))
        assertTrue(cache.contains("prefetch"))
        assertTrue(cache.weight() <= 4)
    }

    @Test
    fun clearRetainsLeasedEntriesAndEvictsUnleasedEntries() {
        val evicted = mutableListOf<String>()
        val cache = BoundedLruCache<String, String>(
            maxWeight = 10,
            weightOf = { it.length },
            onEvicted = { evicted.add(it) }
        )

        cache.put("leased", "one")
        cache.put("idle", "two")
        cache.acquire("leased")
        cache.clear()

        assertTrue(cache.contains("leased"))
        assertFalse(cache.contains("idle"))
        assertEquals(listOf("two"), evicted)

        cache.release("leased")
        cache.clear()
        assertFalse(cache.contains("leased"))
    }
}
