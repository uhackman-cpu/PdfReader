package com.uhackman.pdfreader

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer
import java.util.concurrent.Executors
import kotlin.math.max

class MainActivity : Activity() {
    private val io = Executors.newSingleThreadExecutor()
    private lateinit var store: HighlightStore
    private lateinit var pageList: RecyclerView
    private lateinit var pageInfo: TextView
    private lateinit var statusText: TextView
    private lateinit var searchInput: EditText
    private lateinit var selectedTextView: TextView
    private lateinit var highlightButton: Button

    private var session: PdfSession? = null
    private var currentPage = 0
    private var selectedRange: IntRange? = null
    private var pendingSearchHit: SearchHit? = null
    private var pendingHighlight: Highlight? = null
    private var searchHits: List<SearchHit> = emptyList()
    private var searchHitIndex = -1
    private var pageAdapter: PdfPageAdapter? = null
    private val bitmapCache = PageBitmapCache()
    private val boundPageViews = mutableMapOf<PdfPageView, BoundPage>()
    private val pendingRenderKeys = mutableSetOf<String>()
    private val pendingTileKeys = mutableSetOf<String>()
    private val pageTileManifests = mutableMapOf<Int, List<CachedPageTile>>()
    private val pendingTextPageIndexes = mutableSetOf<String>()
    private val requestedRenderDpiByPage = mutableMapOf<Int, Float>()
    private var totalSearchHitCount = 0L
    private var searchResultsTruncated = false
    private var searchGeneration = 0L
    private var selectedPageIndex = -1
    private var documentSelection: DocumentSelection? = null
    private var selectionAnchor: SelectionEndpoint? = null
    private var selectionFocus: SelectionEndpoint? = null
    private var activeSelectionGesture = false
    private var selectionRequestGeneration = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PDFBoxResourceLoader.init(applicationContext)
        store = HighlightStore(this)
        buildUi()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(8, 8, 8, 8)
        }

        val toolbar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        toolbar.addView(makeButton("Open") { choosePdf() }, weightedButtonParams())
        toolbar.addView(makeButton("Prev") { changePage(-1) }, weightedButtonParams())
        toolbar.addView(makeButton("Next") { changePage(1) }, weightedButtonParams())
        toolbar.addView(makeButton("Highlights") { showHighlights() }, weightedButtonParams())
        root.addView(toolbar, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        val searchRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        searchInput = EditText(this).apply {
            hint = "Search document text"
            setSingleLine(true)
            setTextColor(Color.BLACK)
            textSize = 15f
        }
        searchRow.addView(searchInput, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        searchRow.addView(makeButton("Find") { findText() })
        searchRow.addView(makeButton("Next match") { nextSearchHit() })
        root.addView(searchRow, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        pageInfo = TextView(this).apply {
            text = "No document"
            setTextColor(Color.DKGRAY)
            textSize = 13f
            setPadding(4, 4, 4, 4)
        }
        root.addView(pageInfo)

        pageList = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            itemAnimator = null
            setHasFixedSize(false)
            setItemViewCacheSize(2)
            recycledViewPool.setMaxRecycledViews(0, 3)
            clipToPadding = false
            setPadding(0, 8, 0, 8)
            setBackgroundColor(Color.rgb(238, 238, 238))
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    updateVisiblePages()
                }
            })
        }
        root.addView(pageList, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f
        ))

        statusText = TextView(this).apply {
            text = "Open a text-based PDF. Long-press a word or drag across text to select it."
            setTextColor(Color.DKGRAY)
            textSize = 12f
            setPadding(4, 5, 4, 5)
        }
        root.addView(statusText)

        val selectionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        selectedTextView = TextView(this).apply {
            text = "Selected text will appear here"
            setTextColor(Color.BLACK)
            textSize = 13f
            maxLines = 2
            setPadding(4, 4, 4, 4)
        }
        selectionRow.addView(selectedTextView, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        highlightButton = makeButton("Highlight") { saveHighlight() }
        highlightButton.isEnabled = false
        selectionRow.addView(highlightButton)
        root.addView(selectionRow, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        setContentView(root)
    }

    private fun weightedButtonParams() =
        LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)

    private fun makeButton(label: String, action: () -> Unit): Button =
        Button(this).apply {
            text = label
            textSize = 12f
            setOnClickListener { action() }
        }

    private fun choosePdf() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        startActivityForResult(intent, REQUEST_OPEN_PDF)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_OPEN_PDF || resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        val readFlag = data.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
        if (readFlag != 0) {
            try {
                contentResolver.takePersistableUriPermission(uri, readFlag)
            } catch (_: SecurityException) {
                // Some document providers grant a temporary read permission only.
            }
        }
        openPdf(uri)
    }

    private fun openPdf(uri: Uri) {
        statusText.text = "Opening PDF and extracting positioned text…"
        val previous = session
        session = null
        io.execute {
            var document: PDDocument? = null
            try {
                val input = contentResolver.openInputStream(uri)
                    ?: throw IllegalStateException("Unable to open the selected document")
                val loadedDocument = input.use { PDDocument.load(it) }
                document = loadedDocument
                val pages = GlyphTextStripper.describePages(loadedDocument)
                val documentKey = uri.toString()
                val storedHighlights = store.list(documentKey).toMutableList()
                val loaded = PdfSession(
                    document = loadedDocument,
                    renderer = PDFRenderer(loadedDocument),
                    pages = pages,
                    pageTextCache = PageTextCache(),
                    documentKey = documentKey,
                    storedHighlights = storedHighlights,
                    resolvedHighlights = mutableListOf()
                )
                document = null
                runOnUiThread {
                    try {
                        previous?.document?.close()
                    } catch (_: Exception) {
                    }
                    releaseAllBoundPageViews()
                    previous?.pageTextCache?.clear()
                    pageList.adapter = null
                    session = loaded
                    currentPage = 0
                    selectedPageIndex = -1
                    selectedRange = null
                    documentSelection = null
                    selectionAnchor = null
                    selectionFocus = null
                    activeSelectionGesture = false
                    selectionRequestGeneration++
                    pendingSearchHit = null
                    pendingHighlight = null
                    searchHits = emptyList()
                    searchHitIndex = -1
                    requestedRenderDpiByPage.clear()
                    pageTileManifests.clear()
                    pendingRenderKeys.clear()
                    pendingTileKeys.clear()
                    pendingTextPageIndexes.clear()
                    totalSearchHitCount = 0L
                    searchResultsTruncated = false
                    searchGeneration++
                    pageAdapter = PdfPageAdapter(
                        pageCount = loaded.pages.size,
                        pageAspectRatio = { index ->
                            val page = loaded.pages[index]
                            page.heightPoints / page.widthPoints.coerceAtLeast(1f)
                        },
                        onPageBound = { index, view -> bindPage(index, view) },
                        onPageRecycled = { _, view -> releasePageView(view) },
                        onSelectionChanged = { index, start, end ->
                            val pageView = boundPageViews.entries.firstOrNull {
                                it.value.pageIndex == index
                            }?.key
                            val selectedText = pageView?.textForGlyphRange(start, end)
                            if (selectedText != null) {
                                selectedPageIndex = index
                                selectedRange = minOf(start, end)..maxOf(start, end)
                                selectedTextView.text = selectedText
                                highlightButton.isEnabled = selectedText.isNotBlank()
                            }
                        },
                        onDocumentSelectionGesture = { _, event ->
                            handleDocumentSelectionGesture(event)
                        },
                        onResolutionRequested = { index, dpi ->
                            requestSharperPage(index, dpi)
                        }
                    )
                    pageList.adapter = pageAdapter
                    pageInfo.text = "Page 1 / " + loaded.pages.size
                    statusText.text = "Continuous scrolling • page text and rasters load on demand"
                    pageList.scrollToPosition(0)
                    updateVisiblePages()
                }
            } catch (error: Exception) {
                try {
                    document?.close()
                } catch (_: Exception) {
                }
                runOnUiThread {
                    statusText.text = "Could not open PDF: " +
                        (error.message ?: error.javaClass.simpleName)
                }
            }
        }
    }

    private fun bindPage(pageIndex: Int, view: PdfPageView) {
        val active = session ?: return
        if (pageIndex !in active.pages.indices) return
        boundPageViews.remove(view)?.let { previous ->
            previous.cacheKey?.let(bitmapCache::release)
            previous.tileKeys.forEach(bitmapCache::release)
            if (previous.textLease) previous.textCache?.release(previous.pageIndex)
        }

        val requestedDpi = requestedRenderDpiByPage[pageIndex] ?: PdfPageView.BASE_RENDER_DPI
        val baseDpi = PdfPageView.BASE_RENDER_DPI
        val key = pageBitmapKey(active, pageIndex, baseDpi)
        val cached = bitmapCache.acquire(key)
        val pageText = active.pageTextCache.acquire(pageIndex)
        boundPageViews[view] = BoundPage(
            pageIndex = pageIndex,
            cacheKey = if (cached != null) key else null,
            textCache = active.pageTextCache,
            textLease = pageText != null,
            tileKeys = emptyList()
        )
        view.setPage(
            pageText,
            cached,
            active.resolvedHighlights.filter { it.pageIndex == pageIndex }
        )
        if (pageText != null) {
            resolveStoredHighlightsForPage(active, pageIndex, pageText)
            view.setHighlights(active.resolvedHighlights.filter { it.pageIndex == pageIndex })
        } else {
            requestPageText(pageIndex)
        }
        applyPendingPageState(pageIndex, view)
        applySelectionToBoundPage(pageIndex, view)
        if (requestedDpi > baseDpi * 1.08f) {
            if (!displayCachedTiles(active, pageIndex, requestedDpi)) {
                requestPageTiles(pageIndex, requestedDpi)
            }
        }
        if (cached == null) requestPageRender(pageIndex, baseDpi)
    }

    private fun requestPageText(pageIndex: Int) {
        val active = session ?: return
        if (pageIndex !in active.pages.indices) return

        // Search may populate the cache between bindPage's acquire and this
        // request. Attach that cached page instead of silently returning and
        // leaving the already-bound view without a text layer.
        val cachedPage = active.pageTextCache.acquire(pageIndex)
        if (cachedPage != null) {
            active.pageTextCache.release(pageIndex)
            attachPageTextToBoundViews(active, pageIndex, cachedPage)
            return
        }

        val requestKey = active.documentKey + "|" + pageIndex
        if (!pendingTextPageIndexes.add(requestKey)) return

        io.execute {
            val extracted = try {
                synchronized(active.document) {
                    GlyphTextStripper().extractPage(active.document, pageIndex)
                }
            } catch (error: Exception) {
                null
            }
            runOnUiThread {
                pendingTextPageIndexes.remove(requestKey)
                if (session !== active) return@runOnUiThread
                if (extracted == null) {
                    statusText.text = "Text extraction failed on page " + (pageIndex + 1)
                    return@runOnUiThread
                }

                active.pageTextCache.put(pageIndex, extracted)
                attachPageTextToBoundViews(active, pageIndex, extracted)
            }
        }
    }

    private fun attachPageTextToBoundViews(
        active: PdfSession,
        pageIndex: Int,
        fallbackPageText: PageText
    ) {
        val targets = boundPageViews.entries.filter { it.value.pageIndex == pageIndex }
        for ((view, binding) in targets) {
            if (binding.textLease) continue
            val leased = active.pageTextCache.acquire(pageIndex)
            val pageText = leased ?: fallbackPageText
            view.setPageText(pageText)
            boundPageViews[view] = binding.copy(
                textCache = active.pageTextCache,
                textLease = leased != null
            )
            resolveStoredHighlightsForPage(active, pageIndex, pageText)
            view.setHighlights(active.resolvedHighlights.filter { it.pageIndex == pageIndex })
            applyPendingPageState(pageIndex, view)
            applySelectionToBoundPage(pageIndex, view)
        }
    }

    private fun resolveStoredHighlightsForPage(
        active: PdfSession,
        pageIndex: Int,
        pageText: PageText
    ) {
        val unresolved = active.storedHighlights.filter { saved ->
            saved.pageIndex == pageIndex &&
                active.resolvedHighlights.none { it.id == saved.id }
        }
        for (saved in unresolved) {
            val anchor = HighlightAnchorResolver.resolveInPages(
                sequenceOf(pageIndex to pageText),
                saved
            ) ?: continue
            active.resolvedHighlights.add(
                saved.copy(
                    pageIndex = anchor.pageIndex,
                    startGlyph = anchor.glyphRange.first,
                    endGlyph = anchor.glyphRange.last
                )
            )
        }
    }

    private fun applySelectionToBoundPage(pageIndex: Int, view: PdfPageView) {
        val selection = documentSelection
        if (selection != null) {
            val item = selection.ranges.firstOrNull { it.pageIndex == pageIndex }
            val firstEndpoint = DocumentSelectionBuilder.firstEndpoint(selection.anchor, selection.focus)
            val lastEndpoint = DocumentSelectionBuilder.lastEndpoint(selection.anchor, selection.focus)
            view.setDocumentSelectionRange(
                item?.let { it.startGlyph..it.endGlyph },
                showStartHandle = pageIndex == firstEndpoint.pageIndex,
                showEndHandle = pageIndex == lastEndpoint.pageIndex,
                finished = true,
                startHandleFixedEndpoint = lastEndpoint.takeIf { pageIndex == firstEndpoint.pageIndex },
                endHandleFixedEndpoint = firstEndpoint.takeIf { pageIndex == lastEndpoint.pageIndex }
            )
            return
        }
        val anchor = selectionAnchor ?: return
        val focus = selectionFocus ?: return
        val page = view.getPageText() ?: return
        val range = DocumentSelectionBuilder.rangeForPage(anchor, focus, pageIndex, page.glyphs.size)
        val firstEndpoint = DocumentSelectionBuilder.firstEndpoint(anchor, focus)
        val lastEndpoint = DocumentSelectionBuilder.lastEndpoint(anchor, focus)
        view.setDocumentSelectionRange(
            range,
            showStartHandle = pageIndex == firstEndpoint.pageIndex,
            showEndHandle = pageIndex == lastEndpoint.pageIndex,
            finished = !activeSelectionGesture,
            startHandleFixedEndpoint = lastEndpoint.takeIf { pageIndex == firstEndpoint.pageIndex },
            endHandleFixedEndpoint = firstEndpoint.takeIf { pageIndex == lastEndpoint.pageIndex }
        )
    }

    private fun applyPendingPageState(pageIndex: Int, view: PdfPageView) {
        pendingSearchHit?.takeIf { it.pageIndex == pageIndex }?.let { hit ->
            view.setSearchRange(hit.startChar, hit.endChar)
            pendingSearchHit = null
        }
        pendingHighlight?.takeIf { it.pageIndex == pageIndex }?.let { item ->
            view.setSelectionRange(item.startGlyph, item.endGlyph)
            selectedPageIndex = pageIndex
            selectedRange = item.startGlyph..item.endGlyph
            selectedTextView.text = item.text
            highlightButton.isEnabled = true
            pendingHighlight = null
        }
    }

    private fun releasePageView(view: PdfPageView) {
        val bound = boundPageViews.remove(view) ?: return
        bound.cacheKey?.let(bitmapCache::release)
        bound.tileKeys.forEach(bitmapCache::release)
        if (bound.textLease) bound.textCache?.release(bound.pageIndex)
    }

    private fun releaseAllBoundPageViews() {
        boundPageViews.toList().forEach { (view, bound) ->
            view.clearPage()
            bound.cacheKey?.let(bitmapCache::release)
            bound.tileKeys.forEach(bitmapCache::release)
            if (bound.textLease) bound.textCache?.release(bound.pageIndex)
        }
        boundPageViews.clear()
        pageTileManifests.clear()
        pendingTileKeys.clear()
        bitmapCache.clear()
    }

    private fun pageBitmapKey(active: PdfSession, pageIndex: Int, dpi: Float): String =
        active.documentKey + "|" + pageIndex + "|" + dpi.toInt()

    private fun pageTileKey(
        active: PdfSession,
        pageIndex: Int,
        dpi: Float,
        bounds: PageTileBounds
    ): String = pageBitmapKey(active, pageIndex, dpi) +
        "|tile|" + bounds.leftPx + "|" + bounds.topPx

    private fun requestPageRender(pageIndex: Int, requestedDpi: Float) {
        val active = session ?: return
        val pageData = active.pages.getOrNull(pageIndex) ?: return
        val targetDpi = RenderDpiPolicy.cap(
            pageData.widthPoints,
            pageData.heightPoints,
            requestedDpi
        )
        val key = pageBitmapKey(active, pageIndex, targetDpi)
        if (bitmapCache.contains(key)) {
            displayCachedBitmap(active, pageIndex, targetDpi, key)
            return
        }
        if (!pendingRenderKeys.add(key)) return

        io.execute {
            var rendered: Bitmap? = null
            var failure: Exception? = null
            try {
                rendered = synchronized(active.document) {
                    active.renderer.renderImageWithDPI(pageIndex, targetDpi)
                }
            } catch (error: Exception) {
                failure = error
            }
            runOnUiThread {
                pendingRenderKeys.remove(key)
                if (session !== active) {
                    rendered?.recycle()
                    return@runOnUiThread
                }
                val image = rendered
                if (image == null) {
                    if (targetDpi > PdfPageView.BASE_RENDER_DPI) {
                        requestedRenderDpiByPage[pageIndex] = PdfPageView.BASE_RENDER_DPI
                    }
                    statusText.text = "Page render failed: " +
                        (failure?.message ?: "unknown error")
                    return@runOnUiThread
                }
                bitmapCache.put(key, image)
                displayCachedBitmap(active, pageIndex, targetDpi, key)
            }
        }
    }

    private fun displayCachedBitmap(
        active: PdfSession,
        pageIndex: Int,
        dpi: Float,
        key: String
    ) {
        val entry = boundPageViews.entries.firstOrNull {
            it.value.pageIndex == pageIndex
        } ?: return
        val view = entry.key
        val oldBinding = entry.value
        if (oldBinding.cacheKey == key) return
        val image = bitmapCache.acquire(key) ?: return
        if (active.pages.getOrNull(pageIndex) == null) {
            bitmapCache.release(key)
            return
        }
        view.replaceBitmap(image)
        boundPageViews[view] = oldBinding.copy(cacheKey = key)
        oldBinding.cacheKey?.let(bitmapCache::release)
        applyPendingPageState(pageIndex, view)
    }

    private fun displayCachedTiles(
        active: PdfSession,
        pageIndex: Int,
        dpi: Float
    ): Boolean {
        val manifest = pageTileManifests[pageIndex] ?: return false
        val expectedPrefix = pageBitmapKey(active, pageIndex, dpi) + "|tile|"
        if (manifest.firstOrNull()?.key?.startsWith(expectedPrefix) != true) return false
        val entry = boundPageViews.entries.firstOrNull {
            it.value.pageIndex == pageIndex
        } ?: return false
        val view = entry.key
        val binding = entry.value
        val keys = manifest.map { it.key }
        if (binding.tileKeys == keys) return true

        val acquired = mutableListOf<PageTile>()
        for (item in manifest) {
            val bitmap = bitmapCache.acquire(item.key)
            if (bitmap == null) {
                acquired.forEach { bitmapCache.release(it.cacheKey) }
                return false
            }
            acquired.add(
                PageTile(
                    bounds = item.bounds,
                    pageWidth = item.pageWidth,
                    pageHeight = item.pageHeight,
                    bitmap = bitmap,
                    cacheKey = item.key
                )
            )
        }
        view.replaceTiles(acquired)
        boundPageViews[view] = binding.copy(tileKeys = keys)
        binding.tileKeys.forEach(bitmapCache::release)
        return true
    }

    private fun requestPageTiles(pageIndex: Int, requestedDpi: Float) {
        val active = session ?: return
        val pageData = active.pages.getOrNull(pageIndex) ?: return
        val targetDpi = RenderDpiPolicy.capForTiles(
            pageData.widthPoints,
            pageData.heightPoints,
            requestedDpi
        )
        if (targetDpi <= PdfPageView.BASE_RENDER_DPI * 1.08f) return

        val prefix = pageBitmapKey(active, pageIndex, targetDpi) + "|tile|"
        val manifest = pageTileManifests[pageIndex]
        if (manifest?.firstOrNull()?.key?.startsWith(prefix) == true) {
            val allTilesCached = manifest.all { bitmapCache.contains(it.key) }
            val pageIsBound = boundPageViews.values.any { it.pageIndex == pageIndex }
            if (allTilesCached &&
                (!pageIsBound || displayCachedTiles(active, pageIndex, targetDpi))
            ) return
        }

        val requestKey = pageBitmapKey(active, pageIndex, targetDpi) + "|tiles"
        if (!pendingTileKeys.add(requestKey)) return

        io.execute {
            var tiles: List<PageTile>? = null
            var fullPage: Bitmap? = null
            var failure: Exception? = null
            try {
                val renderedPage = synchronized(active.document) {
                    active.renderer.renderImageWithDPI(pageIndex, targetDpi)
                }
                fullPage = renderedPage
                tiles = PageTileFactory.split(renderedPage, PageTilePlanner.DEFAULT_TILE_SIZE)
                fullPage = null
            } catch (error: Exception) {
                failure = error
            } finally {
                fullPage?.let { if (!it.isRecycled) it.recycle() }
            }

            runOnUiThread {
                pendingTileKeys.remove(requestKey)
                if (session !== active) {
                    tiles?.forEach {
                        if (!it.bitmap.isRecycled) it.bitmap.recycle()
                    }
                    return@runOnUiThread
                }
                val renderedTiles = tiles
                if (renderedTiles == null) {
                    statusText.text = "High-resolution tile render failed: " +
                        (failure?.message ?: "unknown error")
                    return@runOnUiThread
                }

                val nextManifest = mutableListOf<CachedPageTile>()
                var complete = true
                for (index in renderedTiles.indices) {
                    val tile = renderedTiles[index]
                    val key = pageTileKey(active, pageIndex, targetDpi, tile.bounds)
                    if (!bitmapCache.put(key, tile.bitmap)) {
                        complete = false
                        for (remaining in index + 1 until renderedTiles.size) {
                            val bitmap = renderedTiles[remaining].bitmap
                            if (!bitmap.isRecycled) bitmap.recycle()
                        }
                        break
                    }
                    nextManifest.add(
                        CachedPageTile(
                            key = key,
                            bounds = tile.bounds,
                            pageWidth = tile.pageWidth,
                            pageHeight = tile.pageHeight
                        )
                    )
                }

                if (!complete || nextManifest.size != renderedTiles.size) {
                    pageTileManifests.remove(pageIndex)
                    statusText.text =
                        "High-resolution tiles exceed the bitmap cache budget; base page retained"
                    return@runOnUiThread
                }

                pageTileManifests[pageIndex] = nextManifest
                val pageIsBound = boundPageViews.values.any { it.pageIndex == pageIndex }
                if (pageIsBound && !displayCachedTiles(active, pageIndex, targetDpi)) {
                    pageTileManifests.remove(pageIndex)
                    statusText.text = "Tile cache pressure; showing the base page raster"
                }
            }
        }
    }

    private fun updateVisiblePages() {
        val active = session ?: return
        val layout = pageList.layoutManager as? LinearLayoutManager ?: return
        val first = layout.findFirstVisibleItemPosition()
        val last = layout.findLastVisibleItemPosition()
        if (first == RecyclerView.NO_POSITION || last == RecyclerView.NO_POSITION) return

        currentPage = ((first + last) / 2).coerceIn(active.pages.indices)
        pageInfo.text = "Page " + (currentPage + 1) + " / " + active.pages.size

        val preloadFirst = max(0, first - 1)
        val preloadLast = minOf(active.pages.lastIndex, last + 1)
        for (index in preloadFirst..preloadLast) {
            requestPageText(index)
            requestPageRender(index, PdfPageView.BASE_RENDER_DPI)
            val dpi = requestedRenderDpiByPage[index] ?: PdfPageView.BASE_RENDER_DPI
            if (dpi > PdfPageView.BASE_RENDER_DPI * 1.08f) {
                requestPageTiles(index, dpi)
            }
        }
    }

    private fun clearVisibleSelections() {
        selectionRequestGeneration++
        documentSelection = null
        selectionAnchor = null
        selectionFocus = null
        activeSelectionGesture = false
        boundPageViews.keys.forEach { it.clearSelection() }
        selectedRange = null
        selectedPageIndex = -1
        selectedTextView.text = "Selected text will appear here"
        highlightButton.isEnabled = false
    }

    private fun handleDocumentSelectionGesture(event: SelectionGestureEvent) {
        if (event.cancelled) {
            clearVisibleSelections()
            statusText.text = "Selection cancelled"
            return
        }
        if (event.started) {
            selectionAnchor = SelectionEndpoint(
                event.anchorPageIndex,
                minOf(event.anchorStartGlyph, event.anchorEndGlyph),
                maxOf(event.anchorStartGlyph, event.anchorEndGlyph)
            )
            selectionFocus = null
            documentSelection = null
            selectionRequestGeneration++
            activeSelectionGesture = true
            selectedRange = null
            selectedPageIndex = -1
            highlightButton.isEnabled = false
        }
        val anchor = selectionAnchor ?: return
        val target = findSelectionTarget(event.rawX, event.rawY)
        if (target != null) {
            val pageText = target.view.getPageText()
            if (pageText != null) {
                val focusRange = if (event.wordMode) {
                    TextSelection.wordRangeForGlyph(pageText, target.glyphIndex)
                        ?: (target.glyphIndex..target.glyphIndex)
                } else target.glyphIndex..target.glyphIndex
                selectionFocus = SelectionEndpoint(
                    target.pageIndex, focusRange.first, focusRange.last
                )
                applySelectionPreview(anchor, selectionFocus!!, event.finished)
            }
        }
        if (event.finished) {
            activeSelectionGesture = false
            selectionFocus?.let { resolveDocumentSelection(anchor, it) }
        }
    }

    private fun findSelectionTarget(rawX: Float, rawY: Float): SelectionTarget? {
        val listLocation = IntArray(2)
        pageList.getLocationOnScreen(listLocation)
        val listX = rawX - listLocation[0]
        val listY = rawY - listLocation[1]
        if (listX < 0f || listX > pageList.width ||
            listY < 0f || listY > pageList.height
        ) return null
        val edge = (56f * resources.displayMetrics.density).toInt()
        if (listY < edge) {
            pageList.scrollBy(0, -max(2, ((edge - listY) * 0.35f).toInt()))
        } else if (listY > pageList.height - edge) {
            pageList.scrollBy(0, max(2, ((listY - (pageList.height - edge)) * 0.35f).toInt()))
        }

        var bestView: PdfPageView? = null
        var bestBinding: BoundPage? = null
        var bestLeft = 0f
        var bestTop = 0f
        var bestScore = Float.POSITIVE_INFINITY
        for ((view, binding) in boundPageViews) {
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            val left = location[0].toFloat()
            val top = location[1].toFloat()
            val right = left + view.width
            val bottom = top + view.height
            val dx = when {
                rawX < left -> left - rawX
                rawX > right -> rawX - right
                else -> 0f
            }
            val dy = when {
                rawY < top -> top - rawY
                rawY > bottom -> rawY - bottom
                else -> 0f
            }
            val score = dy * 4f + dx
            if (score < bestScore) {
                bestScore = score
                bestView = view
                bestBinding = binding
                bestLeft = left
                bestTop = top
            }
        }
        val targetView = bestView ?: return null
        val binding = bestBinding ?: return null
        if (targetView.getPageText() == null) {
            requestPageText(binding.pageIndex)
            return null
        }
        val localX = (rawX - bestLeft).coerceIn(0f, targetView.width.toFloat())
        val localY = (rawY - bestTop).coerceIn(0f, targetView.height.toFloat())
        val glyphIndex = targetView.glyphAtLocal(localX, localY, true)
        if (glyphIndex < 0) return null
        return SelectionTarget(binding.pageIndex, targetView, glyphIndex)
    }

    private fun applySelectionPreview(
        anchor: SelectionEndpoint,
        focus: SelectionEndpoint,
        finished: Boolean
    ) {
        val firstEndpoint = DocumentSelectionBuilder.firstEndpoint(anchor, focus)
        val lastEndpoint = DocumentSelectionBuilder.lastEndpoint(anchor, focus)
        val firstPage = firstEndpoint.pageIndex
        val lastPage = lastEndpoint.pageIndex
        for ((view, binding) in boundPageViews) {
            val page = view.getPageText()
            val range = page?.let {
                DocumentSelectionBuilder.rangeForPage(anchor, focus, binding.pageIndex, it.glyphs.size)
            }
            view.setDocumentSelectionRange(
                range,
                showStartHandle = binding.pageIndex == firstPage,
                showEndHandle = binding.pageIndex == lastPage,
                finished = finished,
                startHandleFixedEndpoint = lastEndpoint.takeIf { binding.pageIndex == firstPage },
                endHandleFixedEndpoint = firstEndpoint.takeIf { binding.pageIndex == lastPage }
            )
        }
        if (anchor.pageIndex == focus.pageIndex) {
            val page = boundPageViews.entries.firstOrNull {
                it.value.pageIndex == focus.pageIndex
            }?.key?.getPageText()
            val range = page?.let {
                DocumentSelectionBuilder.rangeForPage(anchor, focus, focus.pageIndex, it.glyphs.size)
            }
            selectedTextView.text = if (page != null && range != null) {
                page.textForGlyphRange(range.first, range.last)
            } else "Selecting text…"
        } else {
            selectedTextView.text = "Selecting pages " +
                (firstPage + 1) + "–" + (lastPage + 1) + "…"
        }
        highlightButton.isEnabled = false
    }

    private fun resolveDocumentSelection(
        anchor: SelectionEndpoint,
        focus: SelectionEndpoint
    ) {
        val active = session ?: return
        val generation = ++selectionRequestGeneration
        statusText.text = "Preparing selection across pages…"
        io.execute {
            val builder = DocumentSelectionBuilder.streaming(anchor, focus, active.pages.size)
            var failure: Exception? = null
            try {
                synchronized(active.document) {
                    GlyphTextStripper().forEachPage(
                        active.document, builder.firstPageIndex, builder.lastPageIndex
                    ) { pageIndex, pageText ->
                        active.pageTextCache.put(pageIndex, pageText)
                        builder.accept(pageIndex, pageText)
                    }
                }
            } catch (error: Exception) {
                failure = error
            }
            val resolved = if (failure == null) builder.result() else null
            runOnUiThread {
                if (session !== active || selectionRequestGeneration != generation) return@runOnUiThread
                if (resolved == null) {
                    documentSelection = null
                    selectedTextView.text = "Could not resolve selected text"
                    highlightButton.isEnabled = false
                    statusText.text = "Cross-page selection could not be resolved"
                    return@runOnUiThread
                }
                documentSelection = resolved
                selectionAnchor = resolved.anchor
                selectionFocus = resolved.focus
                val firstEndpoint = DocumentSelectionBuilder.firstEndpoint(resolved.anchor, resolved.focus)
                val lastEndpoint = DocumentSelectionBuilder.lastEndpoint(resolved.anchor, resolved.focus)
                for ((view, binding) in boundPageViews) {
                    val item = resolved.ranges.firstOrNull { it.pageIndex == binding.pageIndex }
                    view.setDocumentSelectionRange(
                        item?.let { it.startGlyph..it.endGlyph },
                        showStartHandle = binding.pageIndex == firstEndpoint.pageIndex,
                        showEndHandle = binding.pageIndex == lastEndpoint.pageIndex,
                        finished = true,
                        startHandleFixedEndpoint = lastEndpoint.takeIf { binding.pageIndex == firstEndpoint.pageIndex },
                        endHandleFixedEndpoint = firstEndpoint.takeIf { binding.pageIndex == lastEndpoint.pageIndex }
                    )
                }
                selectedTextView.text = resolved.selectedText
                highlightButton.isEnabled = resolved.ranges.isNotEmpty()
                statusText.text = "Selected across " + resolved.ranges.size + " page(s)"
            }
        }
    }

    private fun requestSharperPage(pageIndex: Int, requestedDpi: Float) {
        val active = session ?: return
        val pageData = active.pages.getOrNull(pageIndex) ?: return
        val desiredDpi = requestedDpi.coerceIn(
            PdfPageView.BASE_RENDER_DPI,
            PdfPageView.MAX_RENDER_DPI
        )
        val targetDpi = RenderDpiPolicy.capForTiles(
            pageData.widthPoints,
            pageData.heightPoints,
            desiredDpi
        )
        val currentDpi = requestedRenderDpiByPage[pageIndex] ?: PdfPageView.BASE_RENDER_DPI
        if (targetDpi <= currentDpi * 1.08f) return
        requestedRenderDpiByPage[pageIndex] = targetDpi
        requestPageTiles(pageIndex, targetDpi)
    }

    private fun changePage(delta: Int) {
        val active = session ?: return
        val next = currentPage + delta
        if (next !in active.pages.indices) return
        currentPage = next
        pageList.smoothScrollToPosition(next)
    }

    private fun findText() {
        val active = session ?: run {
            Toast.makeText(this, "Open a PDF first", Toast.LENGTH_SHORT).show()
            return
        }
        val query = searchInput.text.toString().trim()
        if (query.isEmpty()) {
            Toast.makeText(this, "Enter text to search", Toast.LENGTH_SHORT).show()
            return
        }

        val generation = ++searchGeneration
        statusText.text = "Searching document text page by page…"
        io.execute {
            val results = SearchResultAccumulator()
            try {
                synchronized(active.document) {
                    GlyphTextStripper().forEachPage(active.document) { pageIndex, pageText ->
                        active.pageTextCache.put(pageIndex, pageText)
                        var from = 0
                        while (from < pageText.text.length) {
                            val found = pageText.text.indexOf(query, from, ignoreCase = true)
                            if (found < 0) break
                            val end = found + query.length
                            if (pageText.glyphRangeForChars(found, end) != null) {
                                results.add(SearchHit(pageIndex, found, end))
                            }
                            from = found + max(1, query.length)
                        }
                    }
                }
            } catch (error: Exception) {
                runOnUiThread {
                    if (session === active && searchGeneration == generation) {
                        statusText.text = "Search failed: " +
                            (error.message ?: error.javaClass.simpleName)
                    }
                }
                return@execute
            }

            runOnUiThread {
                if (session !== active || searchGeneration != generation) return@runOnUiThread
                val snapshot = results.snapshot()
                searchHits = snapshot.hits
                totalSearchHitCount = snapshot.totalMatches
                searchResultsTruncated = snapshot.truncated
                searchHitIndex = if (searchHits.isEmpty()) -1 else 0
                if (searchHits.isEmpty()) {
                    statusText.text = "No text matches found."
                    pageInfo.text = "Page " + (currentPage + 1) + " / " + active.pages.size
                    clearVisibleSelections()
                    return@runOnUiThread
                }
                showSearchHit(searchHits[0])
                pageInfo.text = "Page " + (searchHits[0].pageIndex + 1) + " / " +
                    active.pages.size + " • " + searchMatchSummary()
                statusText.text = if (searchResultsTruncated) {
                    "Found " + totalSearchHitCount + " matches; showing first " +
                        searchHits.size + " for navigation"
                } else {
                    "Found " + totalSearchHitCount + " matches"
                }
            }
        }
    }

    private fun nextSearchHit() {
        if (searchHits.isEmpty()) {
            findText()
            return
        }
        searchHitIndex = (searchHitIndex + 1) % searchHits.size
        val hit = searchHits[searchHitIndex]
        showSearchHit(hit)
        val active = session ?: return
        pageInfo.text = "Page " + (hit.pageIndex + 1) + " / " +
            active.pages.size + " • match " + (searchHitIndex + 1) + " of " +
            searchHits.size + (if (searchResultsTruncated) {
                " (first of " + totalSearchHitCount + ")"
            } else "")
    }

    private fun searchMatchSummary(): String =
        if (searchResultsTruncated) {
            totalSearchHitCount.toString() + " matches; first " + searchHits.size + " retained"
        } else {
            totalSearchHitCount.toString() + " matches"
        }

    private fun showSearchHit(hit: SearchHit) {
        clearVisibleSelections()
        currentPage = hit.pageIndex
        pendingHighlight = null
        pendingSearchHit = hit
        pageList.scrollToPosition(hit.pageIndex)
        val visible = boundPageViews.entries.firstOrNull {
            it.value.pageIndex == hit.pageIndex
        }?.key
        if (visible != null) {
            visible.setSearchRange(hit.startChar, hit.endChar)
            pendingSearchHit = null
        }
    }

    private fun saveDocumentSelection(active: PdfSession, selection: DocumentSelection) {
        if (selection.ranges.isEmpty() || selection.selectedText.isBlank()) return
        val noteInput = EditText(this).apply {
            hint = "Optional note"
            minLines = 1
            setTextColor(Color.BLACK)
        }
        AlertDialog.Builder(this)
            .setTitle("Save highlight across " + selection.ranges.size + " page(s)")
            .setMessage(selection.selectedText)
            .setView(noteInput)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                val note = noteInput.text.toString()
                val groupId = java.util.UUID.randomUUID().toString()
                val drafts = selection.ranges.map { range ->
                    HighlightDraft(
                        range.pageIndex, range.startGlyph, range.endGlyph,
                        range.selectedText, Color.YELLOW, note,
                        range.startChar, range.endChar, range.prefixText, range.suffixText,
                        groupId
                    )
                }
                val saved = try {
                    store.addBatch(active.documentKey, drafts)
                } catch (error: Exception) {
                    statusText.text = "Could not save highlight: " +
                        (error.message ?: error.javaClass.simpleName)
                    emptyList()
                }
                if (saved.size == drafts.size) {
                    active.storedHighlights.addAll(saved)
                    active.resolvedHighlights.addAll(saved)
                    for ((view, binding) in boundPageViews) {
                        view.setHighlights(active.resolvedHighlights.filter {
                            it.pageIndex == binding.pageIndex
                        })
                    }
                    statusText.text = "Highlight saved across " + saved.size + " page(s)"
                } else {
                    statusText.text = "Could not save all page highlights"
                }
            }
            .show()
    }

    private fun saveHighlight() {
        val active = session ?: return
        documentSelection?.let {
            saveDocumentSelection(active, it)
            return
        }
        val range = selectedRange ?: return
        val pageIndex = selectedPageIndex
        val page = boundPageViews.entries.firstOrNull {
            it.value.pageIndex == pageIndex
        }?.key?.getPageText() ?: return
        val selectedText = page.textForGlyphRange(range.first, range.last)
        if (selectedText.isBlank()) return

        val noteInput = EditText(this).apply {
            hint = "Optional note"
            minLines = 1
            setTextColor(Color.BLACK)
        }
        AlertDialog.Builder(this)
            .setTitle("Save highlight")
            .setMessage(selectedText)
            .setView(noteInput)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                val firstGlyph = page.glyphs[range.first]
                val lastGlyph = page.glyphs[range.last]
                val startChar = firstGlyph.startChar
                val endChar = lastGlyph.endChar
                val prefixText = page.text.substring(
                    max(0, startChar - ANCHOR_CONTEXT_LENGTH),
                    startChar
                )
                val suffixText = page.text.substring(
                    endChar,
                    minOf(page.text.length, endChar + ANCHOR_CONTEXT_LENGTH)
                )
                val createdAt = System.currentTimeMillis()
                val note = noteInput.text.toString()
                val id = store.add(
                    documentKey = active.documentKey,
                    pageIndex = pageIndex,
                    startGlyph = range.first,
                    endGlyph = range.last,
                    text = selectedText,
                    color = Color.YELLOW,
                    note = note,
                    startChar = startChar,
                    endChar = endChar,
                    prefixText = prefixText,
                    suffixText = suffixText
                )
                if (id >= 0L) {
                    val saved = Highlight(
                        id = id,
                        pageIndex = pageIndex,
                        startGlyph = range.first,
                        endGlyph = range.last,
                        text = selectedText,
                        color = Color.YELLOW,
                        note = note,
                        createdAt = createdAt,
                        startChar = startChar,
                        endChar = endChar,
                        prefixText = prefixText,
                        suffixText = suffixText
                    )
                    active.storedHighlights.add(saved)
                    active.resolvedHighlights.add(saved)
                    boundPageViews.entries.firstOrNull {
                        it.value.pageIndex == pageIndex
                    }?.key?.setHighlights(
                        active.resolvedHighlights.filter { it.pageIndex == pageIndex }
                    )
                    statusText.text = "Highlight saved"
                } else {
                    statusText.text = "Could not save highlight"
                }
            }
            .show()
    }

    private fun showHighlights() {
        val active = session ?: return
        val items = active.storedHighlights.sortedWith(
            compareBy<Highlight> { it.pageIndex }.thenBy { it.startGlyph }
        )
        if (items.isEmpty()) {
            Toast.makeText(this, "No saved highlights in this document", Toast.LENGTH_SHORT).show()
            return
        }
        val groups = items.groupBy {
            it.groupId.ifBlank { "single:" + it.id }
        }.values.map { group ->
            group.sortedWith(compareBy<Highlight> { it.pageIndex }.thenBy { it.startGlyph })
        }
        val resolvedById = active.resolvedHighlights.associateBy { it.id }
        val labels = groups.map { group ->
            val firstPage = group.minOf { it.pageIndex } + 1
            val lastPage = group.maxOf { it.pageIndex } + 1
            val pageLabel = if (firstPage == lastPage) "Page " + firstPage
                else "Pages " + firstPage + "–" + lastPage
            val excerpt = group.joinToString(" … ") { it.text.replace('\n', ' ').trim() }.take(72)
            val note = group.firstOrNull { it.note.isNotBlank() }?.note?.let { " — " + it } ?: ""
            val pending = if (group.any { resolvedById[it.id] == null }) {
                " • some locations will be searched"
            } else ""
            pageLabel + ": " + excerpt + note + pending
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Document highlights (" + groups.size + ")")
            .setItems(labels) { _, which ->
                val group = groups[which]
                val resolved = group.mapNotNull { resolvedById[it.id] }.firstOrNull()
                if (resolved == null) locateHighlightAndNavigate(active, group.first())
                else navigateToResolvedHighlight(active, resolved)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun locateHighlightAndNavigate(active: PdfSession, saved: Highlight) {
        statusText.text = "Locating saved highlight across the document…"
        io.execute {
            val resolver = HighlightAnchorResolver.streaming(saved)
            val anchor = try {
                synchronized(active.document) {
                    GlyphTextStripper().forEachPage(active.document) { pageIndex, pageText ->
                        active.pageTextCache.put(pageIndex, pageText)
                        resolver.accept(pageIndex, pageText)
                    }
                }
                resolver.result()
            } catch (_: Exception) {
                null
            }
            runOnUiThread {
                if (session !== active) return@runOnUiThread
                if (anchor == null) {
                    Toast.makeText(
                        this,
                        "The saved text could not be located in this PDF.",
                        Toast.LENGTH_SHORT
                    ).show()
                    statusText.text = "Saved highlight location not found"
                    return@runOnUiThread
                }
                val resolved = saved.copy(
                    pageIndex = anchor.pageIndex,
                    startGlyph = anchor.glyphRange.first,
                    endGlyph = anchor.glyphRange.last
                )
                active.resolvedHighlights.removeAll { it.id == resolved.id }
                active.resolvedHighlights.add(resolved)
                navigateToResolvedHighlight(active, resolved)
            }
        }
    }

    private fun navigateToResolvedHighlight(active: PdfSession, resolved: Highlight) {
        clearVisibleSelections()
        currentPage = resolved.pageIndex.coerceIn(0, active.pages.lastIndex)
        pendingHighlight = resolved
        pendingSearchHit = null
        pageList.scrollToPosition(currentPage)
        val visible = boundPageViews.entries.firstOrNull {
            it.value.pageIndex == currentPage
        }?.key
        if (visible != null) applyPendingPageState(currentPage, visible)
        statusText.text = "Opened saved highlight"
    }

    override fun onDestroy() {
        val old = session
        session = null
        searchGeneration++
        pendingTileKeys.clear()
        releaseAllBoundPageViews()
        pageList.adapter = null
        pendingRenderKeys.clear()
        pendingTextPageIndexes.clear()
        old?.pageTextCache?.clear()
        if (old != null) {
            io.execute {
                try {
                    old.document.close()
                } catch (_: Exception) {
                }
            }
        }
        io.shutdown()
        super.onDestroy()
    }

    private data class SelectionTarget(
        val pageIndex: Int,
        val view: PdfPageView,
        val glyphIndex: Int
    )

    private data class BoundPage(
        val pageIndex: Int,
        val cacheKey: String?,
        val textCache: PageTextCache?,
        val textLease: Boolean,
        val tileKeys: List<String> = emptyList()
    )

    private data class CachedPageTile(
        val key: String,
        val bounds: PageTileBounds,
        val pageWidth: Int,
        val pageHeight: Int
    )

    private data class PdfSession(
        val document: PDDocument,
        val renderer: PDFRenderer,
        val pages: List<PageDescriptor>,
        val pageTextCache: PageTextCache,
        val documentKey: String,
        val storedHighlights: MutableList<Highlight>,
        val resolvedHighlights: MutableList<Highlight>
    )

    companion object {
        private const val REQUEST_OPEN_PDF = 7104
        private const val ANCHOR_CONTEXT_LENGTH = 48
    }
}
