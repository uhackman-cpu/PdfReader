package com.uhackman.pdfreader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlin.math.max
import kotlin.math.min

class PdfPageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var page: PageText? = null
    private var bitmap: Bitmap? = null
    private var highlights: List<Highlight> = emptyList()
    private var searchRange: IntRange? = null
    private var selectionRange: IntRange? = null

    var onSelectionChanged: ((Int, Int) -> Unit)? = null

    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val hintPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        textSize = 38f
    }

    private var zoom = 1f
    private var offsetX = 0f
    private var offsetY = 0f
    private var selecting = false
    private var panning = false
    private var selectionAnchor = -1
    private var lastTouchX = 0f
    private var lastTouchY = 0f

    private val scaleDetector = ScaleGestureDetector(
        context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val nextZoom = (zoom * detector.scaleFactor).coerceIn(1f, 5f)
                val ratio = nextZoom / zoom
                offsetX = detector.focusX - (detector.focusX - offsetX) * ratio
                offsetY = detector.focusY - (detector.focusY - offsetY) * ratio
                zoom = nextZoom
                if (zoom <= 1.01f) {
                    zoom = 1f
                    offsetX = 0f
                    offsetY = 0f
                }
                invalidate()
                return true
            }
        }
    )

    init {
        isClickable = true
        setBackgroundColor(Color.rgb(242, 242, 242))
    }

    fun setPage(pageText: PageText, pageBitmap: Bitmap, pageHighlights: List<Highlight>) {
        val oldBitmap = bitmap
        page = pageText
        bitmap = pageBitmap
        highlights = pageHighlights
        searchRange = null
        selectionRange = null
        selectionAnchor = -1
        zoom = 1f
        offsetX = 0f
        offsetY = 0f
        if (oldBitmap != null && oldBitmap !== pageBitmap && !oldBitmap.isRecycled) {
            oldBitmap.recycle()
        }
        invalidate()
    }

    fun setHighlights(items: List<Highlight>) {
        highlights = items
        invalidate()
    }

    fun setSearchRange(startChar: Int, endChar: Int) {
        searchRange = page?.glyphRangeForChars(startChar, endChar)
        invalidate()
    }

    fun setSelectionRange(startGlyph: Int, endGlyph: Int) {
        selectionRange = min(startGlyph, endGlyph)..max(startGlyph, endGlyph)
        selectionAnchor = -1
        invalidate()
    }

    fun clearSelection() {
        selectionRange = null
        selectionAnchor = -1
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val image = bitmap
        if (image == null) {
            canvas.drawText("Open a PDF to begin", 28f, height / 2f, hintPaint)
            return
        }

        val rect = pageRect(image)
        canvas.save()
        canvas.translate(offsetX, offsetY)
        canvas.scale(zoom, zoom)
        canvas.drawBitmap(image, null, rect, bitmapPaint)

        for (highlight in highlights) {
            drawGlyphRange(
                canvas,
                rect,
                highlight.startGlyph..highlight.endGlyph,
                Color.argb(105, Color.red(highlight.color), Color.green(highlight.color), Color.blue(highlight.color))
            )
        }

        searchRange?.let {
            drawGlyphRange(canvas, rect, it, Color.argb(105, 66, 133, 244))
        }
        selectionRange?.let {
            drawGlyphRange(canvas, rect, it, Color.argb(120, 255, 152, 0))
        }
        canvas.restore()
    }

    private fun drawGlyphRange(canvas: Canvas, pageRect: RectF, range: IntRange, color: Int) {
        val currentPage = page ?: return
        if (currentPage.glyphs.isEmpty()) return
        val first = range.first.coerceIn(currentPage.glyphs.indices)
        val last = range.last.coerceIn(currentPage.glyphs.indices)
        if (last < first) return

        overlayPaint.color = color
        for (index in first..last) {
            val glyph = currentPage.glyphs[index]
            val left = pageRect.left + glyph.left * pageRect.width()
            val top = pageRect.top + glyph.top * pageRect.height()
            val right = pageRect.left + glyph.right * pageRect.width()
            val bottom = pageRect.top + glyph.bottom * pageRect.height()
            if (right > left && bottom > top) {
                canvas.drawRect(left, top, right, bottom, overlayPaint)
            }
        }
    }

    private fun pageRect(image: Bitmap): RectF {
        if (width <= 0 || height <= 0 || image.width <= 0 || image.height <= 0) {
            return RectF()
        }
        val fit = min(width.toFloat() / image.width, height.toFloat() / image.height)
        val pageWidth = image.width * fit
        val pageHeight = image.height * fit
        val left = (width - pageWidth) / 2f
        val top = (height - pageHeight) / 2f
        return RectF(left, top, left + pageWidth, top + pageHeight)
    }

    private fun hitTest(viewX: Float, viewY: Float): Int {
        val currentPage = page ?: return -1
        val image = bitmap ?: return -1
        val rect = pageRect(image)
        if (rect.width() <= 0f || rect.height() <= 0f) return -1

        val baseX = (viewX - offsetX) / zoom
        val baseY = (viewY - offsetY) / zoom
        val normalizedX = (baseX - rect.left) / rect.width()
        val normalizedY = (baseY - rect.top) / rect.height()
        if (normalizedX < -0.02f || normalizedX > 1.02f ||
            normalizedY < -0.02f || normalizedY > 1.02f
        ) return -1

        var bestIndex = -1
        var bestScore = Float.MAX_VALUE
        currentPage.glyphs.forEachIndexed { index, glyph ->
            val centerX = (glyph.left + glyph.right) / 2f
            val centerY = (glyph.top + glyph.bottom) / 2f
            val glyphWidth = max(glyph.right - glyph.left, 0.006f)
            val glyphHeight = max(glyph.bottom - glyph.top, 0.012f)
            val dx = (normalizedX - centerX) / glyphWidth
            val dy = (normalizedY - centerY) / glyphHeight
            val score = dx * dx + dy * dy
            if (score < bestScore) {
                bestScore = score
                bestIndex = index
            }
        }
        return if (bestScore <= 36f) bestIndex else -1
    }

    private fun notifySelection() {
        val range = selectionRange ?: return
        onSelectionChanged?.invoke(range.first, range.last)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        if (event.pointerCount > 1 || scaleDetector.isInProgress) {
            selecting = false
            panning = false
            return true
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val glyphIndex = hitTest(event.x, event.y)
                if (glyphIndex >= 0) {
                    selectionAnchor = glyphIndex
                    selectionRange = glyphIndex..glyphIndex
                    selecting = true
                    panning = false
                    notifySelection()
                } else {
                    selecting = false
                    panning = zoom > 1.01f
                    lastTouchX = event.x
                    lastTouchY = event.y
                }
                invalidate()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (selecting) {
                    val glyphIndex = hitTest(event.x, event.y)
                    if (glyphIndex >= 0) {
                        selectionRange = min(selectionAnchor, glyphIndex)..max(selectionAnchor, glyphIndex)
                        notifySelection()
                        invalidate()
                    }
                } else if (panning) {
                    offsetX += event.x - lastTouchX
                    offsetY += event.y - lastTouchY
                    lastTouchX = event.x
                    lastTouchY = event.y
                    invalidate()
                }
                return true
            }

            MotionEvent.ACTION_UP -> {
                selecting = false
                panning = false
                performClick()
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                selecting = false
                panning = false
                return true
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
