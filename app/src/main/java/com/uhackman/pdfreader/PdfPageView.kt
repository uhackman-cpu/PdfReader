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
import android.view.ViewConfiguration
import kotlin.math.max
import kotlin.math.min

class PdfPageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var documentPageIndex: Int = -1
    private var page: PageText? = null
    private var glyphSpatialIndex = GlyphSpatialIndex(emptyList())
    private var bitmap: Bitmap? = null
    private var pageTiles: List<PageTile> = emptyList()
    private var highlights: List<Highlight> = emptyList()
    private var searchRange: IntRange? = null
    private var pendingSearchRange: Pair<Int, Int>? = null
    private var selectionRange: IntRange? = null

    var onSelectionChanged: ((Int, Int) -> Unit)? = null
    var onDocumentSelectionGesture: ((SelectionGestureEvent) -> Unit)? = null
    var onResolutionRequested: ((Float, PageTileRegion) -> Unit)? = null

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
    private var wordSelectionHandlesHidden = false
    private var pendingTextPress = false
    private var downGlyphIndex = -1
    private var downX = 0f
    private var downY = 0f
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
    private var wordAnchorRange: IntRange? = null
    private var panning = false
    private var selectionAnchor = -1
    private var selectionFocus = -1
    // 0 = lower text-unit endpoint, 1 = upper endpoint.
    private var draggingEndpoint = -1
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var gestureAnchorRange: IntRange? = null
    private var gestureAnchorPageIndex = -1
    private var gestureWordMode = false
    private var documentGestureStarted = false
    private var selectionStartHandleVisible = false
    private var selectionEndHandleVisible = false
    private var startHandleFixedEndpoint: SelectionEndpoint? = null
    private var endHandleFixedEndpoint: SelectionEndpoint? = null

    private val scaleDetector = ScaleGestureDetector(
        context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                parent?.requestDisallowInterceptTouchEvent(true)
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
                parent?.requestDisallowInterceptTouchEvent(false)
                val targetDpi = (BASE_RENDER_DPI * zoom).coerceAtMost(MAX_RENDER_DPI)
                if (targetDpi > BASE_RENDER_DPI * 1.25f) {
                    onResolutionRequested?.invoke(targetDpi, visiblePageRegion() ?: PageTileRegion.FULL_PAGE)
                }
            }
        }
    )

    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(event: MotionEvent): Boolean = true

            override fun onLongPress(event: MotionEvent) {
                if (!pendingTextPress || panning || draggingEndpoint >= 0) return
                val currentPage = page ?: return
                val range = TextSelection.wordRangeForGlyph(currentPage, downGlyphIndex) ?: return
                pendingTextPress = false
                selecting = true
                wordSelecting = true
                wordSelectionHandlesHidden = true
                wordAnchorRange = range
                selectionRange = range
                selectionAnchor = range.first
                selectionFocus = range.last
                parent?.requestDisallowInterceptTouchEvent(true)
                gestureAnchorRange = range
                gestureAnchorPageIndex = documentPageIndex
                gestureWordMode = true
                dispatchDocumentSelection(event, started = true)
                notifySelection()
                invalidate()
            }
        }
    )

    init {
        isClickable = true
        setBackgroundColor(Color.rgb(242, 242, 242))
    }

    fun setPage(pageText: PageText?, pageBitmap: Bitmap?, pageHighlights: List<Highlight>) {
        page = pageText
        glyphSpatialIndex = GlyphSpatialIndex(pageText?.glyphs ?: emptyList())
        bitmap = pageBitmap
        pageTiles = emptyList()
        highlights = pageHighlights
        searchRange = null
        pendingSearchRange = null
        selectionRange = null
        selectionAnchor = -1
        selectionFocus = -1
        draggingEndpoint = -1
        selecting = false
        wordSelecting = false
        wordSelectionHandlesHidden = false
        pendingTextPress = false
        downGlyphIndex = -1
        wordAnchorRange = null
        gestureAnchorRange = null
        gestureAnchorPageIndex = -1
        gestureWordMode = false
        documentGestureStarted = false
        selectionStartHandleVisible = false
        selectionEndHandleVisible = false
        startHandleFixedEndpoint = null
        endHandleFixedEndpoint = null
        zoom = 1f
        offsetX = 0f
        offsetY = 0f
        invalidate()
    }

    /** Installs extracted text after the page bitmap/view has already been bound. */
    fun setPageText(pageText: PageText) {
        page = pageText
        glyphSpatialIndex = GlyphSpatialIndex(pageText.glyphs)
        pendingSearchRange?.let { (start, end) ->
            searchRange = pageText.glyphRangeForChars(start, end)
        }
        pendingSearchRange = null
        invalidate()
    }

    fun getPageText(): PageText? = page

    fun textForGlyphRange(startGlyph: Int, endGlyph: Int): String? =
        page?.textForGlyphRange(startGlyph, endGlyph)

    /** Detaches page data before the RecyclerView returns this view to its pool. */
    fun clearPage() {
        page = null
        glyphSpatialIndex = GlyphSpatialIndex(emptyList())
        bitmap = null
        pageTiles = emptyList()
        highlights = emptyList()
        searchRange = null
        pendingSearchRange = null
        selectionRange = null
        selectionAnchor = -1
        selectionFocus = -1
        draggingEndpoint = -1
        selecting = false
        wordSelecting = false
        wordSelectionHandlesHidden = false
        pendingTextPress = false
        downGlyphIndex = -1
        wordAnchorRange = null
        panning = false
        gestureAnchorRange = null
        gestureAnchorPageIndex = -1
        gestureWordMode = false
        documentGestureStarted = false
        selectionStartHandleVisible = false
        selectionEndHandleVisible = false
        startHandleFixedEndpoint = null
        endHandleFixedEndpoint = null
        zoom = 1f
        offsetX = 0f
        offsetY = 0f
        invalidate()
    }

    /**
     * Swaps in a sharper raster of the same page without resetting zoom,
     * pan, text selection, search matches, or annotation overlays.
     */
    fun replaceBitmap(pageBitmap: Bitmap) {
        // The bounded cache owns this bitmap; views only hold a leased reference.
        bitmap = pageBitmap
        invalidate()
    }

    /** Replaces high-resolution tile overlays while the cache leases their bitmaps. */
    fun replaceTiles(tiles: List<PageTile>) {
        pageTiles = tiles
        invalidate()
    }

    fun setHighlights(items: List<Highlight>) {
        highlights = items
        invalidate()
    }

    fun setSearchRange(startChar: Int, endChar: Int) {
        val currentPage = page
        if (currentPage == null) {
            pendingSearchRange = startChar to endChar
            searchRange = null
        } else {
            pendingSearchRange = null
            searchRange = currentPage.glyphRangeForChars(startChar, endChar)
        }
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
        wordSelectionHandlesHidden = false
        wordAnchorRange = null
        selectionStartHandleVisible = true
        selectionEndHandleVisible = true
        startHandleFixedEndpoint = null
        endHandleFixedEndpoint = null
        invalidate()
    }

    fun setDocumentSelectionRange(
        range: IntRange?,
        showStartHandle: Boolean,
        showEndHandle: Boolean,
        finished: Boolean = false,
        startHandleFixedEndpoint: SelectionEndpoint? = null,
        endHandleFixedEndpoint: SelectionEndpoint? = null
    ) {
        selectionRange = range?.let { min(it.first, it.last)..max(it.first, it.last) }
        selectionStartHandleVisible = range != null && showStartHandle
        selectionEndHandleVisible = range != null && showEndHandle
        this.startHandleFixedEndpoint = startHandleFixedEndpoint
        this.endHandleFixedEndpoint = endHandleFixedEndpoint
        if (finished) wordSelectionHandlesHidden = false
        invalidate()
    }

    fun clearSelection() {
        selectionRange = null
        selectionAnchor = -1
        selectionFocus = -1
        draggingEndpoint = -1
        wordSelecting = false
        wordSelectionHandlesHidden = false
        pendingTextPress = false
        downGlyphIndex = -1
        wordAnchorRange = null
        gestureAnchorRange = null
        gestureAnchorPageIndex = -1
        gestureWordMode = false
        documentGestureStarted = false
        selectionStartHandleVisible = false
        selectionEndHandleVisible = false
        startHandleFixedEndpoint = null
        endHandleFixedEndpoint = null
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val image = bitmap
        if (image == null) {
            val message = if (page == null) "Open a PDF to begin" else "Rendering page…"
            canvas.drawText(message, 28f, height / 2f, hintPaint)
            return
        }

        val rect = pageRect(image)
        canvas.save()
        canvas.translate(offsetX, offsetY)
        canvas.scale(zoom, zoom)
        canvas.drawBitmap(image, null, rect, bitmapPaint)
        for (tile in pageTiles) {
            if (tile.bitmap.isRecycled) continue
            val tileRect = RectF(
                rect.left + tile.left * rect.width(),
                rect.top + tile.top * rect.height(),
                rect.left + tile.right * rect.width(),
                rect.top + tile.bottom * rect.height()
            )
            canvas.drawBitmap(tile.bitmap, null, tileRect, bitmapPaint)
        }

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
            if (!wordSelectionHandlesHidden &&
                (selectionStartHandleVisible || selectionEndHandleVisible)
            ) {
                drawSelectionHandles(
                    canvas, rect, it,
                    selectionStartHandleVisible, selectionEndHandleVisible
                )
            }
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
            val topLeft = PageCoordinateTransform.toView(
                glyph.left, glyph.top,
                pageRect.left, pageRect.top, pageRect.right, pageRect.bottom
            )
            val bottomRight = PageCoordinateTransform.toView(
                glyph.right, glyph.bottom,
                pageRect.left, pageRect.top, pageRect.right, pageRect.bottom
            )
            if (bottomRight.x > topLeft.x && bottomRight.y > topLeft.y) {
                canvas.drawRect(
                    topLeft.x, topLeft.y, bottomRight.x, bottomRight.y, overlayPaint
                )
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
        val point = PageCoordinateTransform.toView(
            normalizedX, glyph.bottom,
            pageRect.left, pageRect.top, pageRect.right, pageRect.bottom
        )
        return PointF(point.x, point.y)
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

    private fun drawSelectionHandles(
        canvas: Canvas,
        pageRect: RectF,
        range: IntRange,
        showStart: Boolean,
        showEnd: Boolean
    ) {
        val density = resources.displayMetrics.density
        val radius = 7f * density / zoom
        val stem = 12f * density / zoom
        handleOutlinePaint.strokeWidth = 2f * density / zoom
        val endpoints = mutableListOf<Pair<Int, Boolean>>()
        if (showStart) endpoints.add(range.first to true)
        if (showEnd) endpoints.add(range.last to false)
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
        val currentPage = page ?: return -1
        val image = bitmap ?: return -1
        if (!selectionStartHandleVisible && !selectionEndHandleVisible) return -1
        val rect = pageRect(image)
        val density = resources.displayMetrics.density
        val radiusSquared = (24f * density) * (24f * density)
        var startDistance = Float.POSITIVE_INFINITY
        var endDistance = Float.POSITIVE_INFINITY
        if (selectionStartHandleVisible) {
            val glyph = currentPage.glyphs.getOrNull(range.first) ?: return -1
            val point = PageCoordinateTransform.toScreen(
                glyph.left, glyph.bottom,
                rect.left, rect.top, rect.right, rect.bottom,
                zoom, offsetX, offsetY
            )
            val x = point.x
            val y = point.y + 19f * density
            startDistance = (viewX - x) * (viewX - x) + (viewY - y) * (viewY - y)
        }
        if (selectionEndHandleVisible) {
            val glyph = currentPage.glyphs.getOrNull(range.last) ?: return -1
            val point = PageCoordinateTransform.toScreen(
                glyph.right, glyph.bottom,
                rect.left, rect.top, rect.right, rect.bottom,
                zoom, offsetX, offsetY
            )
            val x = point.x
            val y = point.y + 19f * density
            endDistance = (viewX - x) * (viewX - x) + (viewY - y) * (viewY - y)
        }
        val startHit = startDistance <= radiusSquared
        val endHit = endDistance <= radiusSquared
        if (!startHit && !endHit) return -1
        return if (startHit && endHit) {
            if (startDistance <= endDistance) 0 else 1
        } else if (startHit) 0 else 1
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

    /** Returns the normalized page area visible after the current zoom and pan. */
    fun visiblePageRegion(): PageTileRegion? {
        val image = bitmap ?: return null
        val rect = pageRect(image)
        if (rect.width() <= 0f || rect.height() <= 0f || zoom <= 0f) return null

        val left = ((-offsetX) / zoom - rect.left) / rect.width()
        val top = ((-offsetY) / zoom - rect.top) / rect.height()
        val right = ((width.toFloat() - offsetX) / zoom - rect.left) / rect.width()
        val bottom = ((height.toFloat() - offsetY) / zoom - rect.top) / rect.height()
        val clippedLeft = left.coerceIn(0f, 1f)
        val clippedTop = top.coerceIn(0f, 1f)
        val clippedRight = right.coerceIn(0f, 1f)
        val clippedBottom = bottom.coerceIn(0f, 1f)
        if (clippedRight <= clippedLeft || clippedBottom <= clippedTop) return null
        return PageTileRegion(clippedLeft, clippedTop, clippedRight, clippedBottom)
    }

    private fun hitTest(viewX: Float, viewY: Float): Int {
        val currentPage = page ?: return -1
        val image = bitmap ?: return -1
        val rect = pageRect(image)
        if (rect.width() <= 0f || rect.height() <= 0f) return -1

        val normalized = PageCoordinateTransform.toNormalized(
            viewX, viewY,
            rect.left, rect.top, rect.right, rect.bottom,
            zoom, offsetX, offsetY
        ) ?: return -1
        if (normalized.x < -0.02f || normalized.x > 1.02f ||
            normalized.y < -0.02f || normalized.y > 1.02f
        ) return -1

        return glyphSpatialIndex.nearestGlyphIndex(normalized.x, normalized.y)
    }

    fun glyphAtLocal(viewX: Float, viewY: Float, allowNearest: Boolean = true): Int {
        val direct = hitTest(viewX, viewY)
        if (direct >= 0 || !allowNearest) return direct
        val image = bitmap ?: return -1
        val rect = pageRect(image)
        val normalized = PageCoordinateTransform.toNormalized(
            viewX, viewY,
            rect.left, rect.top, rect.right, rect.bottom,
            zoom, offsetX, offsetY
        ) ?: return -1
        return glyphSpatialIndex.nearestGlyphIndex(
            normalized.x.coerceIn(0f, 1f),
            normalized.y.coerceIn(0f, 1f),
            100f
        )
    }

    private fun dispatchDocumentSelection(
        event: MotionEvent,
        started: Boolean = false,
        finished: Boolean = false,
        cancelled: Boolean = false
    ) {
        val anchor = gestureAnchorRange ?: return
        onDocumentSelectionGesture?.invoke(
            SelectionGestureEvent(
                anchorPageIndex = gestureAnchorPageIndex.takeIf { it >= 0 } ?: documentPageIndex,
                anchorStartGlyph = anchor.first,
                anchorEndGlyph = anchor.last,
                rawX = event.rawX,
                rawY = event.rawY,
                wordMode = gestureWordMode,
                started = started,
                finished = finished,
                cancelled = cancelled
            )
        )
        if (started) documentGestureStarted = true
        if (finished || cancelled) documentGestureStarted = false
    }

    private fun notifySelection() {
        val range = selectionRange ?: return
        onSelectionChanged?.invoke(range.first, range.last)
    }

    private fun updateDraggedEndpoint(glyphIndex: Int) {
        val range = selectionRange ?: return
        wordSelecting = false
        wordSelectionHandlesHidden = false
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
        if (event.pointerCount == 1 && !scaleDetector.isInProgress) {
            gestureDetector.onTouchEvent(event)
        }
        if (event.pointerCount > 1 || scaleDetector.isInProgress) {
            if (documentGestureStarted) {
                dispatchDocumentSelection(event, finished = true, cancelled = true)
            }
            parent?.requestDisallowInterceptTouchEvent(true)
            pendingTextPress = false
            selecting = false
            wordSelecting = false
            wordAnchorRange = null
            panning = false
            draggingEndpoint = -1
            return true
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                downGlyphIndex = hitTest(event.x, event.y)
                pendingTextPress = downGlyphIndex >= 0
                wordSelecting = false
                wordAnchorRange = null
                gestureAnchorRange = null
                gestureAnchorPageIndex = documentPageIndex
                gestureWordMode = false
                documentGestureStarted = false

                val endpoint = hitTestSelectionHandle(event.x, event.y)
                if (endpoint >= 0) {
                    val range = selectionRange ?: return true
                    val fixed = if (endpoint == 0) startHandleFixedEndpoint else endHandleFixedEndpoint
                    if (fixed != null) {
                        gestureAnchorPageIndex = fixed.pageIndex
                        gestureAnchorRange = fixed.startGlyph..fixed.endGlyph
                    } else {
                        gestureAnchorPageIndex = documentPageIndex
                        gestureAnchorRange = if (endpoint == 0) range.last..range.last
                            else range.first..range.first
                    }
                    draggingEndpoint = endpoint
                    selecting = false
                    pendingTextPress = false
                    panning = false
                    parent?.requestDisallowInterceptTouchEvent(true)
                    return true
                }

                if (zoom > 1.01f) {
                    selecting = false
                    pendingTextPress = false
                    panning = true
                    lastTouchX = event.x
                    lastTouchY = event.y
                    parent?.requestDisallowInterceptTouchEvent(true)
                } else {
                    selecting = false
                    panning = false
                }
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (draggingEndpoint >= 0) {
                    dispatchDocumentSelection(event, started = !documentGestureStarted)
                } else if (panning) {
                    offsetX += event.x - lastTouchX
                    offsetY += event.y - lastTouchY
                    lastTouchX = event.x
                    lastTouchY = event.y
                    invalidate()
                    if (zoom > 1.01f) {
                        val targetDpi = (BASE_RENDER_DPI * zoom).coerceAtMost(MAX_RENDER_DPI)
                        onResolutionRequested?.invoke(
                            targetDpi,
                            visiblePageRegion() ?: PageTileRegion.FULL_PAGE
                        )
                    }
                } else if (pendingTextPress && !selecting) {
                    when (GestureArbitration.classify(
                        event.x - downX, event.y - downY, touchSlop
                    )) {
                        GestureIntent.VERTICAL_SCROLL -> pendingTextPress = false
                        GestureIntent.HORIZONTAL_SELECTION -> {
                            parent?.requestDisallowInterceptTouchEvent(true)
                            pendingTextPress = false
                            selecting = true
                            wordSelecting = false
                            wordSelectionHandlesHidden = false
                            selectionAnchor = downGlyphIndex
                            gestureAnchorPageIndex = documentPageIndex
                            gestureAnchorRange = downGlyphIndex..downGlyphIndex
                            gestureWordMode = false
                            val glyphIndex = hitTest(event.x, event.y)
                            if (glyphIndex >= 0) {
                                selectionFocus = glyphIndex
                                selectionRange = min(selectionAnchor, selectionFocus)..
                                    max(selectionAnchor, selectionFocus)
                                selectionStartHandleVisible = true
                                selectionEndHandleVisible = true
                                notifySelection()
                                invalidate()
                            }
                            dispatchDocumentSelection(event, started = true)
                        }
                        GestureIntent.UNDECIDED -> Unit
                    }
                } else if (selecting) {
                    val glyphIndex = hitTest(event.x, event.y)
                    if (glyphIndex >= 0) {
                        if (wordSelecting) {
                            val anchor = wordAnchorRange
                            val target = page?.let { TextSelection.wordRangeForGlyph(it, glyphIndex) }
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
                    dispatchDocumentSelection(event)
                }
                return true
            }

            MotionEvent.ACTION_UP -> {
                if (documentGestureStarted) dispatchDocumentSelection(event, finished = true)
                selecting = false
                wordSelecting = false
                wordAnchorRange = null
                pendingTextPress = false
                panning = false
                draggingEndpoint = -1
                gestureAnchorRange = null
                gestureAnchorPageIndex = -1
                gestureWordMode = false
                parent?.requestDisallowInterceptTouchEvent(false)
                performClick()
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                if (documentGestureStarted) {
                    dispatchDocumentSelection(event, finished = true, cancelled = true)
                }
                selecting = false
                wordSelecting = false
                wordAnchorRange = null
                pendingTextPress = false
                panning = false
                draggingEndpoint = -1
                gestureAnchorRange = null
                gestureAnchorPageIndex = -1
                gestureWordMode = false
                parent?.requestDisallowInterceptTouchEvent(false)
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
