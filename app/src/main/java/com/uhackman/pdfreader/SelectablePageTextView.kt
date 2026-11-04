package com.uhackman.pdfreader

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView

/**
 * Reflowed, native Android text surface for the currently selected PDF page.
 *
 * The PDF page bitmap remains the visual-layout view. This surface exposes the
 * extracted PDF text as real Android text so users get native long-press,
 * drag-selection handles, selection actions, and clipboard support.
 */
class SelectablePageTextView(context: Context) : TextView(context) {
    var onPageTextSelectionChanged: ((Int, Int, Int) -> Unit)? = null

    var pageIndex: Int = -1
        private set

    var pageText: PageText? = null
        private set

    private var suppressSelectionCallback = false

    init {
        setTextColor(Color.BLACK)
        textSize = 17f
        gravity = Gravity.TOP
        setPadding(dp(18), dp(16), dp(18), dp(16))
        setLineSpacing(dp(5).toFloat(), 1f)
        setBackgroundColor(Color.WHITE)
        setTextIsSelectable(true)
        isFocusable = true
        isFocusableInTouchMode = true
        contentDescription = "Selectable text extracted from the current PDF page"
    }

    fun showPage(index: Int, extracted: PageText) {
        suppressSelectionCallback = true
        pageIndex = index
        pageText = extracted
        text = extracted.text
        setSelection(0, 0)
        scrollTo(0, 0)
        suppressSelectionCallback = false
    }

    fun showLoading(index: Int) {
        suppressSelectionCallback = true
        pageIndex = index
        pageText = null
        text = "Extracting selectable text from page ${index + 1}…"
        suppressSelectionCallback = false
    }

    fun showMessage(index: Int, message: String) {
        suppressSelectionCallback = true
        pageIndex = index
        pageText = null
        text = message
        suppressSelectionCallback = false
    }

    fun selectText(start: Int, end: Int) {
        val value = pageText?.text ?: return
        val first = start.coerceIn(0, value.length)
        val last = end.coerceIn(first, value.length)
        if (last > first) {
            requestFocus()
            setSelection(first, last)
        }
    }

    fun clearNativeSelection() {
        if (pageText == null) return
        suppressSelectionCallback = true
        setSelection(0, 0)
        suppressSelectionCallback = false
    }

    fun clearPage() {
        suppressSelectionCallback = true
        pageIndex = -1
        pageText = null
        text = ""
        suppressSelectionCallback = false
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (suppressSelectionCallback || selStart < 0 || selEnd < 0 || selStart == selEnd) return
        if (pageText == null) return
        onPageTextSelectionChanged?.invoke(pageIndex, minOf(selStart, selEnd), maxOf(selStart, selEnd))
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
