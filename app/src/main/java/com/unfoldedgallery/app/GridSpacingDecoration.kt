package com.unfoldedgallery.app

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingDecoration(
    private val spanCount: Int,
    private val spacingDp: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        val spacing = (spacingDp * view.context.resources.displayMetrics.density).toInt()
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.left = spacing - column * spacing / spanCount
        outRect.right = (column + 1) * spacing / spanCount
        if (position >= spanCount) {
            outRect.top = spacing
        }

        // Force square aspect ratio
        view.post {
            val width = view.width
            if (width > 0) {
                val lp = view.layoutParams
                lp.height = width
                view.layoutParams = lp
            }
        }
    }
}
