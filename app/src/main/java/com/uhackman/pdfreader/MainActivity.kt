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
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer
import java.util.concurrent.Executors
import kotlin.math.max

class MainActivity : Activity() {
    private val io = Executors.newSingleThreadExecutor()
    private lateinit var store: HighlightStore
    private lateinit var pageView: PdfPageView
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

        pageView = PdfPageView(this)
        root.addView(pageView, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f
        ))

        statusText = TextView(this).apply {
            text = "Open a text-based PDF. Drag across text to select it."
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

        pageView.onSelectionChanged = { start, end ->
            val active = session
            val page = active?.pages?.getOrNull(currentPage)
            if (page != null) {
                selectedRange = minOf(start, end)..maxOf(start, end)
                selectedTextView.text = page.textForGlyphRange(start, end)
                highlightButton.isEnabled = selectedTextView.text.isNotBlank()
            }
        }

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
                val loaded = PdfSession(
                    document = loadedDocument,
                    renderer = PDFRenderer(loadedDocument),
                    pages = pages,
                    documentKey = uri.toString()
                )
                document = null
                runOnUiThread {
                    try {
                        previous?.document?.close()
                    } catch (_: Exception) {
                    }
                    session = loaded
                    currentPage = 0
                    selectedRange = null
                    pendingSearchHit = null
                    pendingHighlight = null
                    searchHits = emptyList()
                    searchHitIndex = -1
                    pageInfo.text = "Page 1 / " + loaded.pages.size
                    renderCurrentPage()
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

    private fun renderCurrentPage() {
        val active = session ?: return
        if (active.pages.isEmpty()) {
            statusText.text = "No pages were read from this PDF."
            return
        }
        currentPage = currentPage.coerceIn(0, active.pages.lastIndex)
        val pageIndex = currentPage
        pageInfo.text = "Page " + (pageIndex + 1) + " / " + active.pages.size
        statusText.text = "Rendering page…"
        selectedRange = null
        selectedTextView.text = "Drag across text to select it"
        highlightButton.isEnabled = false

        io.execute {
            var rendered: Bitmap? = null
            var failure: Exception? = null
            try {
                rendered = synchronized(active.document) {
                    active.renderer.renderImageWithDPI(pageIndex, 120f)
                }
            } catch (error: Exception) {
                failure = error
            }
            val pageHighlights = store.list(active.documentKey).filter { it.pageIndex == pageIndex }
            runOnUiThread {
                if (session !== active || currentPage != pageIndex) {
                    rendered?.recycle()
                    return@runOnUiThread
                }
                val image = rendered
                if (image == null) {
                    statusText.text = "Page render failed: " + (failure?.message ?: "unknown error")
                    return@runOnUiThread
                }
                pageView.setPage(active.pages[pageIndex], image, pageHighlights)
                pendingSearchHit?.takeIf { it.pageIndex == pageIndex }?.let { hit ->
                    pageView.setSearchRange(hit.startChar, hit.endChar)
                    pendingSearchHit = null
                }
                pendingHighlight?.takeIf { it.pageIndex == pageIndex }?.let { item ->
                    pageView.setSelectionRange(item.startGlyph, item.endGlyph)
                    selectedRange = item.startGlyph..item.endGlyph
                    selectedTextView.text = item.text
                    highlightButton.isEnabled = true
                    pendingHighlight = null
                }
                val glyphCount = active.pages[pageIndex].glyphs.size
                statusText.text = if (glyphCount == 0) {
                    "No selectable text layer on this page. Scanned PDFs need OCR."
                } else {
                    glyphCount.toString() + " positioned text units • drag to select • pinch to zoom"
                }
            }
        }
    }

    private fun changePage(delta: Int) {
        val active = session ?: return
        val next = currentPage + delta
        if (next !in active.pages.indices) return
        currentPage = next
        pendingSearchHit = null
        pendingHighlight = null
        pageView.clearSelection()
        renderCurrentPage()
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
            pageView.clearSelection()
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
        renderCurrentPage()
    }

    private fun saveHighlight() {
        val active = session ?: return
        val range = selectedRange ?: return
        val page = active.pages.getOrNull(currentPage) ?: return
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
                store.add(
                    documentKey = active.documentKey,
                    pageIndex = currentPage,
                    startGlyph = range.first,
                    endGlyph = range.last,
                    text = selectedText,
                    color = Color.YELLOW,
                    note = noteInput.text.toString()
                )
                val pageHighlights = store.list(active.documentKey).filter { it.pageIndex == currentPage }
                pageView.setHighlights(pageHighlights)
                statusText.text = "Highlight saved"
            }
            .show()
    }

    private fun showHighlights() {
        val active = session ?: return
        val items = store.list(active.documentKey)
        if (items.isEmpty()) {
            Toast.makeText(this, "No saved highlights in this document", Toast.LENGTH_SHORT).show()
            return
        }
        val labels = items.map { item ->
            val note = if (item.note.isBlank()) "" else " — " + item.note
            "Page " + (item.pageIndex + 1) + ": " +
                item.text.replace('\n', ' ').take(72) + note
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Document highlights (" + items.size + ")")
            .setItems(labels) { _, which ->
                val selected = items[which]
                currentPage = selected.pageIndex.coerceIn(0, active.pages.lastIndex)
                pendingHighlight = selected
                pendingSearchHit = null
                renderCurrentPage()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    override fun onDestroy() {
        val old = session
        session = null
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

    private data class PdfSession(
        val document: PDDocument,
        val renderer: PDFRenderer,
        val pages: List<PageText>,
        val documentKey: String
    )

    companion object {
        private const val REQUEST_OPEN_PDF = 7104
    }
}
