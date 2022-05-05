package com.wing.tree.android.wordle.presentation.extention

import android.animation.ObjectAnimator
import android.graphics.Path
import android.view.View
import android.view.animation.*
import com.wing.tree.android.wordle.presentation.R

fun View.scale(from: Float, to: Float, duration: Long, withEndAction: Runnable = Runnable {  }) {
    val pivotXType = Animation.RELATIVE_TO_SELF
    val scaleAnimation = ScaleAnimation(from, to, from, to, pivotXType, 0.5F, pivotXType, 0.5F)

    scaleAnimation.duration = duration

    scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            withEndAction.run()
        }

        override fun onAnimationRepeat(animation: Animation?) {

        }

    })

    startAnimation(scaleAnimation)
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.shake() {
    val animation = AnimationUtils.loadAnimation(context, R.anim.shake).apply {
        fillAfter = true
    }

    val animationSet = AnimationSet(true).apply {
        addAnimation(animation)
    }

    startAnimation(animationSet)
}