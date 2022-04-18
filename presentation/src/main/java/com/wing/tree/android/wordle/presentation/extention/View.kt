package com.wing.tree.android.wordle.presentation.extention

import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation

fun View.scale(from: Float, to: Float, duration: Long, withEndAction: Runnable = Runnable {  }) {
//    pivotX = 50.0F
//    pivotY = 50.0F
//
//    float fromX, float toX, float fromY, float toY
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