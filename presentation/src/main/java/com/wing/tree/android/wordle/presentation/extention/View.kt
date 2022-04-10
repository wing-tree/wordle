package com.wing.tree.android.wordle.presentation.extention

import android.view.View

fun View.scale(value: Float, duration: Long, withEndAction: Runnable = Runnable {  }) {
    animate().apply {
        scaleX(value)
        scaleY(value)
        setDuration(duration)
        withEndAction(withEndAction)
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}