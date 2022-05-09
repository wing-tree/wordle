package com.wing.tree.android.wordle.presentation.extention

import android.animation.Animator
import android.view.View
import android.view.animation.*
import android.widget.TextView
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

fun TextView.textFadeIn(text: CharSequence, duration: Long = 120L) {
    this.text = text

    alpha = 0.0F
    visible()

    animate()
        .alpha(1.0F)
        .setDuration(duration)
        .setInterpolator(DecelerateInterpolator())
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) = Unit

            override fun onAnimationEnd(animation: Animator?) = Unit

            override fun onAnimationCancel(animation: Animator?) {
                alpha = 1.0F
            }

            override fun onAnimationRepeat(animation: Animator?) = Unit
        }).withLayer()
}

fun TextView.textFadeOut(duration: Long = 120L) {
    alpha = 1.0F
    visible()

    animate()
        .alpha(0.0F)
        .setDuration(duration)
        .setInterpolator(AccelerateInterpolator())
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) = Unit

            override fun onAnimationEnd(animation: Animator?) {
                text = null
                gone()
            }

            override fun onAnimationCancel(animation: Animator?) {
                alpha = 0.0F
                text = null
                gone()
            }

            override fun onAnimationRepeat(animation: Animator?) = Unit
        }).withLayer()
}