package com.uhackman.pdfreader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
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
    private var glyphSpatialIndex = GlyphSpatialIndex(emptyList())
    private var bitmap: Bitmap? = null
    private var highlights: List<Highlight> = emptyList()
    private var searchRange: IntRange? = null
    private var selectionRange: IntRange? = null

    var onSelectionChanged: ((Int, Int) -> Unit)? = null
    var onResolutionRequested: ((Float) -> Unit)? = null

    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val hintPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        textSize = 38f
    }
    private val handleFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(25, 118, 210)
        style = Paint.Style.FILL
    }
    private val handleOutlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
    }

    private var zoom = 1f
    private var offsetX = 0f
    private var offsetY = 0f
    private var selecting = false
    private var wordSelecting = false
    private var wordAnchorRange: IntRange? = null
    private var panning = false
    private var selectionAnchor = -1
    private var selectionFocus = -1
    // 0 = lower text-unit endpoint, 1 = upper endpoint.
    private var draggingEndpoint = -1
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

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                val targetDpi = (BASE_RENDER_DPI * zoom).coerceAtMost(MAX_RENDER_DPI)
                if (targetDpi > BASE_RENDER_DPI * 1.25f) {
                    onResolutionRequested?.invoke(targetDpi)
                }
            }
        }
    )

    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(event: MotionEvent): Boolean = true

            override fun onLongPress(event: MotionEvent) {
                if (!selecting || panning || draggingEndpoint >= 0) return
                val currentPage = page ?: return
                val glyphIndex = hitTest(event.x, event.y)
                val range = TextSelection.wordRangeForGlyph(currentPage, glyphIndex) ?: return
                wordSelecting = true
                wordAnchorRange = range
                selectionRange = range
                selectionAnchor = range.first
                selectionFocus = range.last
                notifySelection()
                invalidate()
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
        glyphSpatialIndex = GlyphSpatialIndex(pageText.glyphs)
        bitmap = pageBitmap
        highlights = pageHighlights
        searchRange = null
        selectionRange = null
        selectionAnchor = -1
        selectionFocus = -1
        draggingEndpoint = -1
        wordSelecting = false
        wordAnchorRange = null
        zoom = 1f
        offsetX = 0f
        offsetY = 0f
        if (oldBitmap != null && oldBitmap !== pageBitmap && !oldBitmap.isRecycled) {
            oldBitmap.recycle()
        }
        invalidate()
    }

    /**
     * Swaps in a sharper raster of the same page without resetting zoom,
     * pan, text selection, search matches, or annotation overlays.
     */
    fun replaceBitmap(pageBitmap: Bitmap) {
        val oldBitmap = bitmap
        bitmap = pageBitmap
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
        val first = min(startGlyph, endGlyph)
        val last = max(startGlyph, endGlyph)
        selectionRange = first..last
        selectionAnchor = first
        selectionFocus = last
        draggingEndpoint = -1
        wordSelecting = false
        wordAnchorRange = null
        invalidate()
    }

    fun clearSelection() {
        selectionRange = null
        selectionAnchor = -1
        selectionFocus = -1
        draggingEndpoint = -1
        wordSelecting = false
        wordAnchorRange = null
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
                Color.argb(
                    105,
                    Color.red(highlight.color),
                    Color.green(highlight.color),
                    Color.blue(highlight.color)
                )
            )
        }

        searchRange?.let {
            drawGlyphRange(canvas, rect, it, Color.argb(105, 66, 133, 244))
        }
        selectionRange?.let {
            drawGlyphRange(canvas, rect, it, Color.argb(120, 255, 152, 0))
            drawSelectionHandles(canvas, rect, it)
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

    private fun selectionHandleBasePoint(
        glyphIndex: Int,
        isStart: Boolean,
        pageRect: RectF
    ): PointF {
        val glyph = page?.glyphs?.getOrNull(glyphIndex) ?: return PointF()
        val normalizedX = if (isStart) glyph.left else glyph.right
        return PointF(
            pageRect.left + normalizedX * pageRect.width(),
            pageRect.top + glyph.bottom * pageRect.height()
        )
    }

    private fun selectionHandleCenter(
        glyphIndex: Int,
        isStart: Boolean,
        pageRect: RectF
    ): PointF {
        val base = selectionHandleBasePoint(glyphIndex, isStart, pageRect)
        val density = resources.displayMetrics.density
        val radius = 7f * density / zoom
        val stem = 12f * density / zoom
        return PointF(base.x, base.y + stem + radius)
    }

    private fun drawSelectionHandles(canvas: Canvas, pageRect: RectF, range: IntRange) {
        val density = resources.displayMetrics.density
        val radius = 7f * density / zoom
        val stem = 12f * density / zoom
        handleOutlinePaint.strokeWidth = 2f * density / zoom

        val endpoints = listOf(
            range.first to true,
            range.last to false
        )
        for ((glyphIndex, isStart) in endpoints) {
            val base = selectionHandleBasePoint(glyphIndex, isStart, pageRect)
            val center = selectionHandleCenter(glyphIndex, isStart, pageRect)
            canvas.drawLine(base.x, base.y, base.x, base.y + stem, handleOutlinePaint)
            canvas.drawCircle(center.x, center.y, radius, handleFillPaint)
            canvas.drawCircle(center.x, center.y, radius, handleOutlinePaint)
        }
    }

    private fun hitTestSelectionHandle(viewX: Float, viewY: Float): Int {
        val range = selectionRange ?: return -1
        val image = bitmap ?: return -1
        val rect = pageRect(image)
        val density = resources.displayMetrics.density
        val touchRadius = 24f * density
        val radiusSquared = touchRadius * touchRadius

        val start = selectionHandleCenter(range.first, true, rect)
        val end = selectionHandleCenter(range.last, false, rect)
        val startX = offsetX + start.x * zoom
        val startY = offsetY + start.y * zoom
        val endX = offsetX + end.x * zoom
        val endY = offsetY + end.y * zoom

        val startDistance = (viewX - startX) * (viewX - startX) +
            (viewY - startY) * (viewY - startY)
        val endDistance = (viewX - endX) * (viewX - endX) +
            (viewY - endY) * (viewY - endY)

        val startHit = startDistance <= radiusSquared
        val endHit = endDistance <= radiusSquared
        if (!startHit && !endHit) return -1
        return if (startHit && endHit) {
            if (startDistance <= endDistance) 0 else 1
        } else if (startHit) {
            0
        } else {
            1
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

        return glyphSpatialIndex.nearestGlyphIndex(normalizedX, normalizedY)
    }

    private fun notifySelection() {
        val range = selectionRange ?: return
        onSelectionChanged?.invoke(range.first, range.last)
    }

    private fun updateDraggedEndpoint(glyphIndex: Int) {
        val range = selectionRange ?: return
        wordSelecting = false
        wordAnchorRange = null
        if (draggingEndpoint == 0) {
            if (glyphIndex <= range.last) {
                selectionRange = glyphIndex..range.last
            } else {
                selectionRange = range.last..glyphIndex
                draggingEndpoint = 1
            }
        } else if (draggingEndpoint == 1) {
            if (glyphIndex >= range.first) {
                selectionRange = range.first..glyphIndex
            } else {
                selectionRange = glyphIndex..range.first
                draggingEndpoint = 0
            }
        }
        selectionAnchor = selectionRange?.first ?: -1
        selectionFocus = selectionRange?.last ?: -1
        notifySelection()
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        if (event.pointerCount == 1) gestureDetector.onTouchEvent(event)
        if (event.pointerCount > 1 || scaleDetector.isInProgress) {
            selecting = false
            wordSelecting = false
            wordAnchorRange = null
            panning = false
            draggingEndpoint = -1
            return true
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                wordSelecting = false
                wordAnchorRange = null
                val endpoint = hitTestSelectionHandle(event.x, event.y)
                if (endpoint >= 0) {
                    draggingEndpoint = endpoint
                    selecting = false
                    panning = false
                    return true
                }

                val glyphIndex = hitTest(event.x, event.y)
                if (glyphIndex >= 0) {
                    selectionAnchor = glyphIndex
                    selectionFocus = glyphIndex
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
                if (draggingEndpoint >= 0) {
                    val glyphIndex = hitTest(event.x, event.y)
                    if (glyphIndex >= 0) updateDraggedEndpoint(glyphIndex)
                } else if (selecting) {
                    val glyphIndex = hitTest(event.x, event.y)
                    if (glyphIndex >= 0) {
                        if (wordSelecting) {
                            val anchor = wordAnchorRange
                            val target = page?.let {
                                TextSelection.wordRangeForGlyph(it, glyphIndex)
                            }
                            if (anchor != null && target != null) {
                                selectionRange = when {
                                    target.last < anchor.first -> target.first..anchor.last
                                    target.first > anchor.last -> anchor.first..target.last
                                    else -> anchor
                                }
                            }
                        } else {
                            selectionFocus = glyphIndex
                            selectionRange = min(selectionAnchor, selectionFocus)..
                                max(selectionAnchor, selectionFocus)
                        }
                        selectionRange?.let {
                            selectionAnchor = it.first
                            selectionFocus = it.last
                        }
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
                wordSelecting = false
                wordAnchorRange = null
                panning = false
                draggingEndpoint = -1
                performClick()
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                selecting = false
                wordSelecting = false
                wordAnchorRange = null
                panning = false
                draggingEndpoint = -1
                return true
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    companion object {
        const val BASE_RENDER_DPI = 120f
        const val MAX_RENDER_DPI = 288f
    }
}
