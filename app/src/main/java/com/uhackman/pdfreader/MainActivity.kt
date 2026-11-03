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
    private val requestedRenderDpiByPage = mutableMapOf<Int, Float>()
    private var selectedPageIndex = -1

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
                val stripper = GlyphTextStripper()
                val pages = stripper.extract(loadedDocument)
                val documentKey = uri.toString()
                val storedHighlights = store.list(documentKey).toMutableList()
                val resolvedHighlights = storedHighlights.mapNotNull { saved ->
                    val anchor = HighlightAnchorResolver.resolveInDocument(pages, saved)
                        ?: return@mapNotNull null
                    saved.copy(
                        pageIndex = anchor.pageIndex,
                        startGlyph = anchor.glyphRange.first,
                        endGlyph = anchor.glyphRange.last
                    )
                }.toMutableList()
                val loaded = PdfSession(
                    document = loadedDocument,
                    renderer = PDFRenderer(loadedDocument),
                    pages = pages,
                    documentKey = documentKey,
                    storedHighlights = storedHighlights,
                    resolvedHighlights = resolvedHighlights
                )
                document = null
                runOnUiThread {
                    try {
                        previous?.document?.close()
                    } catch (_: Exception) {
                    }
                    releaseAllBoundPageViews()
                    pageList.adapter = null
                    session = loaded
                    currentPage = 0
                    selectedPageIndex = -1
                    selectedRange = null
                    pendingSearchHit = null
                    pendingHighlight = null
                    searchHits = emptyList()
                    searchHitIndex = -1
                    requestedRenderDpiByPage.clear()
                    pendingRenderKeys.clear()
                    pageAdapter = PdfPageAdapter(
                        pageCount = loaded.pages.size,
                        pageAspectRatio = { index ->
                            val page = loaded.pages[index]
                            page.heightPoints / page.widthPoints.coerceAtLeast(1f)
                        },
                        onPageBound = { index, view -> bindPage(index, view) },
                        onPageRecycled = { _, view -> releasePageView(view) },
                        onSelectionChanged = { index, start, end ->
                            val page = loaded.pages.getOrNull(index)
                            if (page != null) {
                                selectedPageIndex = index
                                selectedRange = minOf(start, end)..maxOf(start, end)
                                selectedTextView.text = page.textForGlyphRange(start, end)
                                highlightButton.isEnabled = selectedTextView.text.isNotBlank()
                            }
                        },
                        onResolutionRequested = { index, dpi ->
                            requestSharperPage(index, dpi)
                        }
                    )
                    pageList.adapter = pageAdapter
                    pageInfo.text = "Page 1 / " + loaded.pages.size
                    statusText.text = "Continuous scrolling • nearby pages render on demand"
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
        val pageData = active.pages.getOrNull(pageIndex) ?: return
        boundPageViews.remove(view)?.cacheKey?.let(bitmapCache::release)

        val requestedDpi = requestedRenderDpiByPage[pageIndex] ?: PdfPageView.BASE_RENDER_DPI
        var key = pageBitmapKey(active, pageIndex, requestedDpi)
        var cached = bitmapCache.acquire(key)
        if (cached == null && requestedDpi > PdfPageView.BASE_RENDER_DPI) {
            key = pageBitmapKey(active, pageIndex, PdfPageView.BASE_RENDER_DPI)
            cached = bitmapCache.acquire(key)
        }
        boundPageViews[view] = BoundPage(pageIndex, if (cached != null) key else null)
        view.setPage(
            pageData,
            cached,
            active.resolvedHighlights.filter { it.pageIndex == pageIndex }
        )
        applyPendingPageState(pageIndex, view)
        if (cached == null) requestPageRender(pageIndex, requestedDpi)
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
    }

    private fun releaseAllBoundPageViews() {
        boundPageViews.toList().forEach { (view, bound) ->
            view.clearPage()
            bound.cacheKey?.let(bitmapCache::release)
        }
        boundPageViews.clear()
        bitmapCache.clear()
    }

    private fun pageBitmapKey(active: PdfSession, pageIndex: Int, dpi: Float): String =
        active.documentKey + "|" + pageIndex + "|" + dpi.toInt()

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
        val pageData = active.pages.getOrNull(pageIndex) ?: run {
            bitmapCache.release(key)
            return
        }
        if (dpi <= PdfPageView.BASE_RENDER_DPI * 1.01f) {
            view.setPage(
                pageData,
                image,
                active.resolvedHighlights.filter { it.pageIndex == pageIndex }
            )
        } else {
            view.replaceBitmap(image)
        }
        boundPageViews[view] = BoundPage(pageIndex, key)
        oldBinding.cacheKey?.let(bitmapCache::release)
        applyPendingPageState(pageIndex, view)
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
            val dpi = requestedRenderDpiByPage[index] ?: PdfPageView.BASE_RENDER_DPI
            requestPageRender(index, dpi)
        }
    }

    private fun clearVisibleSelections() {
        boundPageViews.keys.forEach { it.clearSelection() }
        selectedRange = null
        selectedPageIndex = -1
        selectedTextView.text = "Selected text will appear here"
        highlightButton.isEnabled = false
    }

    private fun requestSharperPage(pageIndex: Int, requestedDpi: Float) {
        val active = session ?: return
        val pageData = active.pages.getOrNull(pageIndex) ?: return
        val desiredDpi = requestedDpi.coerceIn(
            PdfPageView.BASE_RENDER_DPI,
            PdfPageView.MAX_RENDER_DPI
        )
        val targetDpi = RenderDpiPolicy.cap(
            pageData.widthPoints,
            pageData.heightPoints,
            desiredDpi
        )
        val currentDpi = requestedRenderDpiByPage[pageIndex] ?: PdfPageView.BASE_RENDER_DPI
        if (targetDpi <= currentDpi * 1.08f) return
        requestedRenderDpiByPage[pageIndex] = targetDpi
        requestPageRender(pageIndex, targetDpi)
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

        val hits = mutableListOf<SearchHit>()
        active.pages.forEachIndexed { pageIndex, page ->
            var from = 0
            while (from < page.text.length) {
                val found = page.text.indexOf(query, from, ignoreCase = true)
                if (found < 0) break
                val end = found + query.length
                if (page.glyphRangeForChars(found, end) != null) {
                    hits.add(SearchHit(pageIndex, found, end))
                }
                from = found + max(1, query.length)
            }
        }
        searchHits = hits
        searchHitIndex = if (hits.isEmpty()) -1 else 0
        if (hits.isEmpty()) {
            statusText.text = "No text matches found."
            clearVisibleSelections()
            return
        }
        showSearchHit(hits[0])
        pageInfo.text = "Page " + (hits[0].pageIndex + 1) + " / " +
            active.pages.size + " • " + hits.size + " matches"
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
            active.pages.size + " • match " + (searchHitIndex + 1) + " of " + searchHits.size
    }

    private fun showSearchHit(hit: SearchHit) {
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

    private fun saveHighlight() {
        val active = session ?: return
        val range = selectedRange ?: return
        val pageIndex = selectedPageIndex
        val page = active.pages.getOrNull(pageIndex) ?: return
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
        val resolvedById = active.resolvedHighlights.associateBy { it.id }
        val labels = items.map { item ->
            val resolved = resolvedById[item.id]
            val pageNumber = (resolved?.pageIndex ?: item.pageIndex) + 1
            val note = if (item.note.isBlank()) "" else " — " + item.note
            val missing = if (resolved == null) " • location not found" else ""
            "Page " + pageNumber + ": " +
                item.text.replace('\\n', ' ').take(72) + note + missing
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Document highlights (" + items.size + ")")
            .setItems(labels) { _, which ->
                val selected = items[which]
                val resolved = resolvedById[selected.id]
                if (resolved == null) {
                    Toast.makeText(
                        this,
                        "The saved text could not be located in this PDF.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setItems
                }
                currentPage = resolved.pageIndex.coerceIn(0, active.pages.lastIndex)
                pendingHighlight = resolved
                pendingSearchHit = null
                pageList.scrollToPosition(currentPage)
                val visible = boundPageViews.entries.firstOrNull {
                    it.value.pageIndex == currentPage
                }?.key
                if (visible != null) {
                    applyPendingPageState(currentPage, visible)
                }
            }
            .setNegativeButton("Close", null)
            .show()
    }

    override fun onDestroy() {
        val old = session
        session = null
        releaseAllBoundPageViews()
        pageList.adapter = null
        pendingRenderKeys.clear()
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

    private data class BoundPage(
        val pageIndex: Int,
        val cacheKey: String?
    )

    private data class PdfSession(
        val document: PDDocument,
        val renderer: PDFRenderer,
        val pages: List<PageText>,
        val documentKey: String,
        val storedHighlights: MutableList<Highlight>,
        val resolvedHighlights: MutableList<Highlight>
    )

    companion object {
        private const val REQUEST_OPEN_PDF = 7104
        private const val ANCHOR_CONTEXT_LENGTH = 48
    }
}
