package com.wing.tree.android.wordle.presentation.widget

import android.view.View

interface Flippable<T: View> {
    var isAnimating: Boolean
    var isFlippable: Boolean

    fun flip(doOnEnd: ((T) -> Unit)? = null)
}