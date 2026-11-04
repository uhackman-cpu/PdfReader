package com.uhackman.pdfreader

import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class PdfPageAdapter(
    private val pageCount: Int,
    private val pageAspectRatio: (Int) -> Float,
    private val onPageBound: (Int, PdfPageView) -> Unit,
    private val onPageRecycled: (Int, PdfPageView) -> Unit,
    private val onSelectionChanged: (Int, Int, Int) -> Unit,
    private val onDocumentSelectionGesture: (Int, SelectionGestureEvent) -> Unit,
    private val onResolutionRequested: (Int, Float, PageTileRegion) -> Unit
) : RecyclerView.Adapter<PdfPageAdapter.PageHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun getItemCount(): Int = pageCount

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
        val pageView = PdfPageView(parent.context).apply {
            setBackgroundColor(Color.WHITE)
        }
        return PageHolder(pageView)
    }

    override fun onBindViewHolder(holder: PageHolder, position: Int) {
        holder.boundPageIndex = position
        holder.pageView.documentPageIndex = position
        val density = holder.pageView.resources.displayMetrics.density
        val measuredWidth = holder.pageView.parent
            ?.let { it as? RecyclerView }
            ?.let { it.width - it.paddingLeft - it.paddingRight }
            ?.takeIf { it > 0 }
            ?: holder.pageView.resources.displayMetrics.widthPixels
        val ratio = pageAspectRatio(position)
            .takeIf { it.isFinite() && it > 0f }
            ?: (842f / 595f)
        val margin = (8f * density).roundToInt()
        val contentHeight = (measuredWidth * ratio)
            .roundToInt()
            .coerceAtLeast((240f * density).roundToInt())

        holder.pageView.layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            contentHeight + margin * 2
        ).apply {
            setMargins(0, margin, 0, margin)
        }

        holder.pageView.onSelectionChanged = { start, end ->
            onSelectionChanged(position, start, end)
        }
        holder.pageView.onDocumentSelectionGesture = { event ->
            onDocumentSelectionGesture(position, event)
        }
        holder.pageView.onResolutionRequested = { dpi, region ->
            onResolutionRequested(position, dpi, region)
        }
        onPageBound(position, holder.pageView)
    }

    override fun onViewRecycled(holder: PageHolder) {
        val pageIndex = holder.boundPageIndex
        holder.pageView.clearPage()
        if (pageIndex >= 0) onPageRecycled(pageIndex, holder.pageView)
        holder.pageView.onSelectionChanged = null
        holder.pageView.onDocumentSelectionGesture = null
        holder.pageView.onResolutionRequested = null
        holder.pageView.documentPageIndex = RecyclerView.NO_POSITION
        holder.boundPageIndex = RecyclerView.NO_POSITION
        super.onViewRecycled(holder)
    }

    class PageHolder(val pageView: PdfPageView) : RecyclerView.ViewHolder(pageView) {
        var boundPageIndex: Int = RecyclerView.NO_POSITION
    }
}
