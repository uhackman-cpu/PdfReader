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


/**
 * Expands a touched text unit to a Unicode-aware word range.
 * Apostrophes and joiners are kept only when they connect word characters.
 */
object TextSelection {
    fun wordRangeForGlyph(page: PageText, glyphIndex: Int): IntRange? {
        val glyph = page.glyphs.getOrNull(glyphIndex) ?: return null
        val text = page.text
        val start = glyph.startChar.coerceIn(0, text.length)
        val end = glyph.endChar.coerceIn(start, text.length)
        if (start >= end) return null

        val firstCodePoint = Character.codePointAt(text, start)
        if (!isWordCodePoint(firstCodePoint) &&
            !isWordConnector(text, start, firstCodePoint)
        ) {
            return page.glyphRangeForChars(start, end)
        }

        var wordStart = start
        while (wordStart > 0) {
            val previousStart = Character.offsetByCodePoints(text, wordStart, -1)
            val previous = Character.codePointAt(text, previousStart)
            if (isWordCodePoint(previous) || isWordConnector(text, previousStart, previous)) {
                wordStart = previousStart
            } else {
                break
            }
        }

        var wordEnd = end
        while (wordEnd < text.length) {
            val next = Character.codePointAt(text, wordEnd)
            if (isWordCodePoint(next) || isWordConnector(text, wordEnd, next)) {
                wordEnd += Character.charCount(next)
            } else {
                break
            }
        }

        return page.glyphRangeForChars(wordStart, wordEnd)
    }

    private fun isWordCodePoint(codePoint: Int): Boolean {
        val type = Character.getType(codePoint)
        return Character.isLetterOrDigit(codePoint) ||
            type == Character.NON_SPACING_MARK.toInt() ||
            type == Character.COMBINING_SPACING_MARK.toInt() ||
            type == Character.ENCLOSING_MARK.toInt() ||
            type == Character.CONNECTOR_PUNCTUATION.toInt()
    }

    private fun isWordConnector(text: String, index: Int, codePoint: Int): Boolean {
        val apostrophe = codePoint == 0x27 || codePoint == 0x2019 || codePoint == 0x02BC
        val joiner = codePoint == 0x200C || codePoint == 0x200D
        if (!apostrophe && !joiner || index <= 0) return false

        val after = index + Character.charCount(codePoint)
        if (after >= text.length) return false
        val before = Character.offsetByCodePoints(text, index, -1)
        return isWordCodePoint(Character.codePointAt(text, before)) &&
            isWordCodePoint(Character.codePointAt(text, after))
    }
}

/**
 * Uniform-grid nearest-glyph index for touch hit-testing.
 *
 * The expanding-ring search uses a conservative lower bound based on the
 * largest glyph dimensions, so it returns the same nearest glyph as a full
 * scan without visiting every glyph on every MotionEvent.
 */
class GlyphSpatialIndex(
    private val glyphs: List<Glyph>,
    private val columns: Int = 64,
    private val rows: Int = 64
) {
    private val cells = arrayOfNulls<MutableList<Int>>(columns * rows)
    private var maxGlyphWidth = 0.006f
    private var maxGlyphHeight = 0.012f

    init {
        require(columns > 0 && rows > 0)
        glyphs.forEachIndexed { index, glyph ->
            val width = (glyph.right - glyph.left).takeIf { it.isFinite() } ?: 0.006f
            val height = (glyph.bottom - glyph.top).takeIf { it.isFinite() } ?: 0.012f
            maxGlyphWidth = maxOf(maxGlyphWidth, width, 0.006f)
            maxGlyphHeight = maxOf(maxGlyphHeight, height, 0.012f)

            val centerX = ((glyph.left + glyph.right) / 2f)
                .takeIf { it.isFinite() }?.coerceIn(0f, 1f) ?: return@forEachIndexed
            val centerY = ((glyph.top + glyph.bottom) / 2f)
                .takeIf { it.isFinite() }?.coerceIn(0f, 1f) ?: return@forEachIndexed
            val column = (centerX * columns).toInt().coerceIn(0, columns - 1)
            val row = (centerY * rows).toInt().coerceIn(0, rows - 1)
            val cellIndex = row * columns + column
            val cell = cells[cellIndex] ?: mutableListOf<Int>().also {
                cells[cellIndex] = it
            }
            cell.add(index)
        }
    }

    fun nearestGlyphIndex(x: Float, y: Float, maxScore: Float = 36f): Int {
        if (glyphs.isEmpty() || !x.isFinite() || !y.isFinite()) return -1
        if (x < -0.02f || x > 1.02f || y < -0.02f || y > 1.02f) return -1

        val centerColumn = (x.coerceIn(0f, 1f) * columns).toInt().coerceIn(0, columns - 1)
        val centerRow = (y.coerceIn(0f, 1f) * rows).toInt().coerceIn(0, rows - 1)
        var bestIndex = -1
        var bestScore = Float.POSITIVE_INFINITY
        var ring = 0

        while (ring < maxOf(columns, rows)) {
            val left = maxOf(0, centerColumn - ring)
            val right = minOf(columns - 1, centerColumn + ring)
            val top = maxOf(0, centerRow - ring)
            val bottom = minOf(rows - 1, centerRow + ring)

            for (row in top..bottom) {
                for (column in left..right) {
                    if (ring != 0 && row != top && row != bottom &&
                        column != left && column != right
                    ) continue

                    val cell = cells[row * columns + column] ?: continue
                    for (index in cell) {
                        val glyph = glyphs[index]
                        val width = maxOf(glyph.right - glyph.left, 0.006f)
                        val height = maxOf(glyph.bottom - glyph.top, 0.012f)
                        val dx = (x - (glyph.left + glyph.right) / 2f) / width
                        val dy = (y - (glyph.top + glyph.bottom) / 2f) / height
                        val score = dx * dx + dy * dy
                        if (score < bestScore ||
                            (score == bestScore && (bestIndex < 0 || index < bestIndex))
                        ) {
                            bestScore = score
                            bestIndex = index
                        }
                    }
                }
            }

            val fullGridVisited = left == 0 && right == columns - 1 &&
                top == 0 && bottom == rows - 1
            if (fullGridVisited) break

            val minDx = minDistanceToUnvisitedCells(x, centerColumn, ring, columns)
            val minDy = minDistanceToUnvisitedCells(y, centerRow, ring, rows)
            val lowerBound = minOf(
                square(minDx / maxGlyphWidth),
                square(minDy / maxGlyphHeight)
            )
            if (lowerBound > minOf(bestScore, maxScore)) break
            ring++
        }

        return if (bestIndex >= 0 && bestScore <= maxScore) bestIndex else -1
    }

    private fun minDistanceToUnvisitedCells(
        coordinate: Float,
        centerCell: Int,
        radius: Int,
        count: Int
    ): Float {
        val firstVisited = maxOf(0, centerCell - radius)
        val lastVisited = minOf(count - 1, centerCell + radius)
        var distance = Float.POSITIVE_INFINITY
        if (firstVisited > 0) {
            distance = minOf(distance, maxOf(0f, coordinate - firstVisited.toFloat() / count))
        }
        if (lastVisited < count - 1) {
            distance = minOf(distance, maxOf(0f, (lastVisited + 1).toFloat() / count - coordinate))
        }
        return distance
    }

    private fun square(value: Float): Float = value * value
}


/**
 * Bounds PDF raster dimensions by page area so zoom-quality upgrades do not
 * allocate unbounded bitmaps for posters, maps, or unusually large sheets.
 */
object RenderDpiPolicy {
    const val DEFAULT_DPI = 120f
    const val MAX_BITMAP_PIXELS = 12_000_000.0
    const val MIN_DPI = 48f

    fun cap(widthPoints: Float, heightPoints: Float, requestedDpi: Float): Float {
        val desired = requestedDpi.takeIf { it.isFinite() && it > 0f } ?: DEFAULT_DPI
        if (!widthPoints.isFinite() || !heightPoints.isFinite() ||
            widthPoints <= 0f || heightPoints <= 0f
        ) return desired

        val areaPoints = widthPoints.toDouble() * heightPoints.toDouble()
        val maxDpi = kotlin.math.sqrt(
            MAX_BITMAP_PIXELS * 72.0 * 72.0 / areaPoints
        ).toFloat()
        return minOf(desired, maxDpi.coerceAtLeast(MIN_DPI))
    }
}


enum class GestureIntent {
    UNDECIDED,
    VERTICAL_SCROLL,
    HORIZONTAL_SELECTION
}

/** Keeps vertical page scrolling distinct from horizontal text selection. */
object GestureArbitration {
    fun classify(dx: Float, dy: Float, touchSlop: Float): GestureIntent {
        if (!dx.isFinite() || !dy.isFinite() || !touchSlop.isFinite()) {
            return GestureIntent.UNDECIDED
        }
        val horizontal = kotlin.math.abs(dx)
        val vertical = kotlin.math.abs(dy)
        if (maxOf(horizontal, vertical) <= touchSlop.coerceAtLeast(0f)) {
            return GestureIntent.UNDECIDED
        }
        return if (vertical > horizontal) {
            GestureIntent.VERTICAL_SCROLL
        } else {
            GestureIntent.HORIZONTAL_SELECTION
        }
    }
}
