package com.wing.tree.android.wordle.presentation.util

import android.animation.AnimatorInflater
import android.view.View
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.extention.gone
import com.wing.tree.android.wordle.presentation.extention.visible

fun flip(`in`: View, `out`: View, doOnEnd: (() -> Unit)? = null) {
    val context = `in`.context

    `in`.visible()

    val flipInAnimationSet = AnimatorInflater.loadAnimator(context, R.animator.flip_in)
    val flipOutAnimatorSet = AnimatorInflater.loadAnimator(context, R.animator.flip_out)

    flipInAnimationSet.setTarget(`in`)
    flipOutAnimatorSet.setTarget(`out`)

    flipInAnimationSet.start()
    flipOutAnimatorSet.start()

    flipInAnimationSet.doOnEnd {
        `out`.gone()
        doOnEnd?.invoke()
    }

    flipInAnimationSet.doOnCancel {
        `out`.gone()
    }
}