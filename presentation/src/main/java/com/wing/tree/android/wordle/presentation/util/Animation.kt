package com.wing.tree.android.wordle.presentation.util

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.view.View
import androidx.core.animation.doOnEnd
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.extention.gone
import com.wing.tree.android.wordle.presentation.extention.visible

fun flip(`in`: View, `out`: View, doOnEnd: (() -> Unit)? = null) {
    val context = `in`.context

    `in`.visible()

    val flipOutAnimatorSet = AnimatorInflater.loadAnimator(context, R.animator.flip_out)
    val flipInAnimationSet = AnimatorInflater.loadAnimator(context, R.animator.flip_in)

    flipOutAnimatorSet.setTarget(`out`)
    flipInAnimationSet.setTarget(`in`)

    flipOutAnimatorSet.start()
    flipInAnimationSet.start()

    flipInAnimationSet.doOnEnd {
        `out`.gone()
        doOnEnd?.invoke()
    }
}