package com.uhackman.pdfreader

import android.graphics.Bitmap
import java.util.LinkedHashMap

/**
 * Weighted access-order cache. Entries currently displayed by a page view are
 * leased and cannot be recycled until that view releases its lease.
 */
class BoundedLruCache<K : Any, V : Any>(
    private val maxWeight: Int,
    private val weightOf: (V) -> Int,
    private val onEvicted: (V) -> Unit = {}
) {
    private class Entry<V>(
        val value: V,
        val weight: Int,
        var leases: Int = 0
    )

    private val entries = LinkedHashMap<K, Entry<V>>(16, 0.75f, true)
    private var currentWeight = 0

    init {
        require(maxWeight > 0)
    }

    @Synchronized
    fun contains(key: K): Boolean = entries.containsKey(key)

    @Synchronized
    fun acquire(key: K): V? {
        val entry = entries[key] ?: return null
        entry.leases += 1
        return entry.value
    }

    @Synchronized
    fun put(key: K, value: V): Boolean {
        val existing = entries[key]
        if (existing != null) {
            if (existing.value === value) return true
            if (existing.leases > 0) {
                onEvicted(value)
                return false
            }
            entries.remove(key)
            currentWeight -= existing.weight
            onEvicted(existing.value)
        }

        val weight = weightOf(value).coerceAtLeast(1)
        entries[key] = Entry(value, weight)
        currentWeight += weight
        trimToWeight()
        return entries[key]?.value === value
    }

    @Synchronized
    fun release(key: K) {
        val entry = entries[key] ?: return
        if (entry.leases > 0) entry.leases -= 1
        trimToWeight()
    }

    @Synchronized
    fun clear() {
        val iterator = entries.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next().value
            if (entry.leases == 0) {
                iterator.remove()
                currentWeight -= entry.weight
                onEvicted(entry.value)
            }
        }
    }

    @Synchronized
    fun weight(): Int = currentWeight

    @Synchronized
    fun entryCount(): Int = entries.size

    private fun trimToWeight() {
        while (currentWeight > maxWeight) {
            val victim = entries.entries.firstOrNull { it.value.leases == 0 } ?: break
            entries.remove(victim.key)
            currentWeight -= victim.value.weight
            onEvicted(victim.value.value)
        }
    }
}

/** Bitmap cache with a strict 64 MiB target and view-safe recycling. */
class PageBitmapCache(
    maxBytes: Int = DEFAULT_MAX_BYTES
) {
    private val cache = BoundedLruCache<String, Bitmap>(
        maxWeight = maxBytes,
        weightOf = { bitmap -> bitmap.byteCount.coerceAtLeast(1) },
        onEvicted = { bitmap ->
            if (!bitmap.isRecycled) bitmap.recycle()
        }
    )

    fun contains(key: String): Boolean = cache.contains(key)
    fun acquire(key: String): Bitmap? = cache.acquire(key)
    fun put(key: String, bitmap: Bitmap): Boolean = cache.put(key, bitmap)
    fun release(key: String) = cache.release(key)
    fun clear() = cache.clear()

    companion object {
        const val DEFAULT_MAX_BYTES = 64 * 1024 * 1024
    }
}
