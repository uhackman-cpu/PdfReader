package com.uhackman.pdfreader

data class Glyph(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val startChar: Int,
    val endChar: Int
)

data class PageText(
    val text: String,
    val glyphs: List<Glyph>,
    val widthPoints: Float,
    val heightPoints: Float
) {
    fun glyphRangeForChars(startChar: Int, endChar: Int): IntRange? {
        if (endChar <= startChar || glyphs.isEmpty()) return null
        val first = glyphs.indexOfFirst { it.endChar > startChar }
        val last = glyphs.indexOfLast { it.startChar < endChar }
        return if (first >= 0 && last >= first) first..last else null
    }

    fun textForGlyphRange(startGlyph: Int, endGlyph: Int): String {
        if (glyphs.isEmpty()) return ""
        val first = minOf(startGlyph, endGlyph).coerceIn(glyphs.indices)
        val last = maxOf(startGlyph, endGlyph).coerceIn(glyphs.indices)
        return text.substring(glyphs[first].startChar, glyphs[last].endChar).trim()
    }
}

data class SearchHit(
    val pageIndex: Int,
    val startChar: Int,
    val endChar: Int
)
