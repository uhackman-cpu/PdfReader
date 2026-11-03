package com.uhackman.pdfreader

import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.text.TextPosition
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Dimensions of the page as PDFRenderer displays it. PDFRenderer swaps the
 * crop-box axes for 90/270 degree page rotations.
 */
data class PageDescriptor(
    val widthPoints: Float,
    val heightPoints: Float,
    val rotationDegrees: Int
)

class GlyphTextStripper : PDFTextStripper() {
    private val builders = mutableListOf<PageBuilder>()
    private var current: PageBuilder? = null

    init {
        setSortByPosition(true)
    }

    override fun startPage(page: PDPage) {
        super.startPage(page)
        val cropBox = page.cropBox
        val dimensions = displayedPageDimensions(cropBox.width, cropBox.height, page.rotation)
        val builder = PageBuilder(
            width = dimensions.x.coerceAtLeast(1f),
            height = dimensions.y.coerceAtLeast(1f)
        )
        builders.add(builder)
        current = builder
    }

    override fun writeString(text: String, textPositions: MutableList<TextPosition>) {
        val builder = current ?: return
        if (builder.text.isNotEmpty()) builder.text.append('\n')

        var previous: TextPosition? = null
        for (position in textPositions) {
            val unicode = position.unicode ?: continue
            if (unicode.isEmpty()) continue

            // Direction-adjusted values are useful for detecting gaps in a text
            // run. Geometry uses page-rotation-adjusted coordinates so it is
            // in the same orientation as PDFRenderer's bitmap.
            val directionX = position.xDirAdj
            val prior = previous
            if (prior != null) {
                val gap = directionX - (prior.xDirAdj + abs(prior.widthDirAdj))
                val referenceHeight = min(abs(prior.heightDir), abs(position.heightDir))
                val threshold = max(1.2f, referenceHeight * 0.25f)
                if (gap > threshold && builder.text.isNotEmpty() && builder.text.last() != ' ') {
                    builder.text.append(' ')
                }
            }

            val start = builder.text.length
            builder.text.append(unicode)
            val end = builder.text.length

            val x = position.x
            val y = position.y
            val glyphWidth = abs(position.width)
            val glyphHeight = abs(position.height)
            val left = (x / builder.width).coerceIn(0f, 1f)
            val top = (y / builder.height).coerceIn(0f, 1f)
            val right = ((x + glyphWidth) / builder.width).coerceIn(left, 1f)
            val bottom = ((y + glyphHeight) / builder.height).coerceIn(top, 1f)

            builder.glyphs.add(Glyph(left, top, right, bottom, start, end))
            previous = position
        }

        super.writeString(text, textPositions)
    }

    fun extract(document: PDDocument): List<PageText> {
        builders.clear()
        current = null
        setStartPage(1)
        setEndPage(document.numberOfPages)
        getText(document)
        return builders.map(::toPageText)
    }

    /** Extracts one page only; callers can keep a bounded cache of the result. */
    fun extractPage(document: PDDocument, pageIndex: Int): PageText {
        require(pageIndex in 0 until document.numberOfPages) {
            "Page index $pageIndex is outside the document"
        }
        builders.clear()
        current = null
        setStartPage(pageIndex + 1)
        setEndPage(pageIndex + 1)
        getText(document)
        val builder = builders.firstOrNull()
        if (builder != null) return toPageText(builder)

        val descriptor = describePage(document.getPage(pageIndex))
        return PageText("", emptyList(), descriptor.widthPoints, descriptor.heightPoints)
    }

    private fun toPageText(builder: PageBuilder): PageText = PageText(
        text = builder.text.toString(),
        glyphs = builder.glyphs.toList(),
        widthPoints = builder.width,
        heightPoints = builder.height
    )

    private class PageBuilder(
        val width: Float,
        val height: Float
    ) {
        val text = StringBuilder()
        val glyphs = mutableListOf<Glyph>()
    }

    companion object {
        fun describePages(document: PDDocument): List<PageDescriptor> =
            (0 until document.numberOfPages).map { index ->
                describePage(document.getPage(index))
            }

        private fun describePage(page: PDPage): PageDescriptor {
            val cropBox = page.cropBox
            val rotation = ((page.rotation % 360) + 360) % 360
            val dimensions = displayedPageDimensions(cropBox.width, cropBox.height, rotation)
            return PageDescriptor(
                widthPoints = dimensions.x.coerceAtLeast(1f),
                heightPoints = dimensions.y.coerceAtLeast(1f),
                rotationDegrees = rotation
            )
        }
    }
}
