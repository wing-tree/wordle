package com.wing.tree.android.wordle.presentation.adapter.billing

import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

internal class SkuDetailsItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val childLayoutPosition = parent.getChildLayoutPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        val unit = TypedValue.COMPLEX_UNIT_DIP
        val value = 8.0F
        val displayMetrics = view.context.resources.displayMetrics

        val bottom = TypedValue.applyDimension(unit, value, displayMetrics)

        if (childLayoutPosition < itemCount) {
            outRect.bottom = bottom.roundToInt()
        }
    }
}