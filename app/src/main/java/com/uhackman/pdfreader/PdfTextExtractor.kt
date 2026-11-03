package com.uhackman.pdfreader

import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.text.TextPosition
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class GlyphTextStripper : PDFTextStripper() {
    private val builders = mutableListOf<PageBuilder>()
    private var current: PageBuilder? = null

    init {
        setSortByPosition(true)
    }

    override fun startPage(page: PDPage) {
        super.startPage(page)
        val cropBox = page.cropBox
        val builder = PageBuilder(
            width = cropBox.width.coerceAtLeast(1f),
            height = cropBox.height.coerceAtLeast(1f)
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

            val x = position.xDirAdj
            val glyphWidth = abs(position.widthDirAdj)
            val glyphHeight = abs(position.heightDir)
            val prior = previous
            if (prior != null) {
                val gap = x - (prior.xDirAdj + abs(prior.widthDirAdj))
                val referenceHeight = min(abs(prior.heightDir), glyphHeight)
                val threshold = max(1.2f, referenceHeight * 0.25f)
                if (gap > threshold && builder.text.isNotEmpty() && builder.text.last() != ' ') {
                    builder.text.append(' ')
                }
            }

            val start = builder.text.length
            builder.text.append(unicode)
            val end = builder.text.length
            val baselineFromTop = position.yDirAdj

            val left = (x / builder.width).coerceIn(0f, 1f)
            val top = ((baselineFromTop - glyphHeight) / builder.height).coerceIn(0f, 1f)
            val right = ((x + glyphWidth) / builder.width).coerceIn(left, 1f)
            val bottom = (baselineFromTop / builder.height).coerceIn(top, 1f)

            builder.glyphs.add(Glyph(left, top, right, bottom, start, end))
            previous = position
        }

        super.writeString(text, textPositions)
    }

    fun extract(document: PDDocument): List<PageText> {
        getText(document)
        return builders.map {
            PageText(
                text = it.text.toString(),
                glyphs = it.glyphs.toList(),
                widthPoints = it.width,
                heightPoints = it.height
            )
        }
    }

    private class PageBuilder(
        val width: Float,
        val height: Float
    ) {
        val text = StringBuilder()
        val glyphs = mutableListOf<Glyph>()
    }
}
