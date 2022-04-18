package com.wing.tree.android.wordle.presentation.widget

import android.view.View

interface Flippable<T: View> {
    var flippable: Boolean
    var isRunning: Boolean

    fun flip(doOnEnd: ((T) -> Unit)? = null)
}