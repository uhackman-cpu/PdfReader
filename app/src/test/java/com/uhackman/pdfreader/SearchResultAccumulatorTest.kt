package com.uhackman.pdfreader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchResultAccumulatorTest {
    @Test
    fun retainsHitsInDocumentOrderAndCountsAllMatches() {
        val accumulator = SearchResultAccumulator(maxRetained = 2)
        accumulator.add(SearchHit(0, 1, 3))
        accumulator.add(SearchHit(2, 4, 6))
        accumulator.add(SearchHit(9, 7, 9))

        val snapshot = accumulator.snapshot()
        assertEquals(listOf(
            SearchHit(0, 1, 3),
            SearchHit(2, 4, 6)
        ), snapshot.hits)
        assertEquals(3L, snapshot.totalMatches)
        assertTrue(snapshot.truncated)
    }

    @Test
    fun exactLimitIsNotMarkedTruncated() {
        val accumulator = SearchResultAccumulator(maxRetained = 2)
        accumulator.add(SearchHit(0, 0, 1))
        accumulator.add(SearchHit(1, 0, 1))

        val snapshot = accumulator.snapshot()
        assertEquals(2, snapshot.hits.size)
        assertEquals(2L, snapshot.totalMatches)
        assertFalse(snapshot.truncated)
    }

    @Test(expected = IllegalArgumentException::class)
    fun requiresPositiveRetentionLimit() {
        SearchResultAccumulator(maxRetained = 0)
    }
}
