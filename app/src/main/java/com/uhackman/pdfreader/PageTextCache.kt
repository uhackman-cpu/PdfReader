package com.uhackman.pdfreader

/**
 * Bounded weighted cache for extracted page text and positioned glyphs.
 * Page views acquire leases while bound; recycling releases the lease so a
 * visible page is never evicted while its spatial index or overlays use it.
 */
class PageTextCache(
    maxBytes: Int = DEFAULT_MAX_BYTES
) {
    private val cache = BoundedLruCache<Int, PageText>(
        maxWeight = maxBytes,
        weightOf = ::estimatedBytes
    )

    fun contains(pageIndex: Int): Boolean = cache.contains(pageIndex)

    fun acquire(pageIndex: Int): PageText? = cache.acquire(pageIndex)

    fun put(pageIndex: Int, page: PageText): Boolean = cache.put(pageIndex, page)

    fun release(pageIndex: Int) = cache.release(pageIndex)

    fun clear() = cache.clear()

    fun weightBytes(): Int = cache.weight()

    fun entryCount(): Int = cache.entryCount()

    private fun estimatedBytes(page: PageText): Int {
        // Include UTF-16 text, glyph object/coordinate overhead and collection
        // storage. This is an intentionally conservative cache budget estimate.
        val bytes = 256L + page.text.length.toLong() * 2L +
            page.glyphs.size.toLong() * ESTIMATED_GLYPH_BYTES
        return bytes.coerceAtMost(Int.MAX_VALUE.toLong()).toInt().coerceAtLeast(1)
    }

    companion object {
        const val DEFAULT_MAX_BYTES = 24 * 1024 * 1024
        private const val ESTIMATED_GLYPH_BYTES = 48L
    }
}
