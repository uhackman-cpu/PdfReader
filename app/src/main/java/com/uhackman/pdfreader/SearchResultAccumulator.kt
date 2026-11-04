package com.uhackman.pdfreader

data class SearchResultsSnapshot(
    val hits: List<SearchHit>,
    val totalMatches: Long,
    val truncated: Boolean
)

/**
 * Retains a bounded navigation window while still counting every match found
 * during the streaming document scan.
 */
class SearchResultAccumulator(
    private val maxRetained: Int = DEFAULT_MAX_RETAINED
) {
    private val retained = ArrayList<SearchHit>(minOf(maxRetained, 256))
    var totalMatches: Long = 0
        private set

    init {
        require(maxRetained > 0)
    }

    fun add(hit: SearchHit) {
        totalMatches += 1
        if (retained.size < maxRetained) retained.add(hit)
    }

    fun snapshot(): SearchResultsSnapshot = SearchResultsSnapshot(
        hits = retained.toList(),
        totalMatches = totalMatches,
        truncated = totalMatches > retained.size
    )

    companion object {
        const val DEFAULT_MAX_RETAINED = 20_000
    }
}
