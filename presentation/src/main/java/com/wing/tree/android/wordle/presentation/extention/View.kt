package com.wing.tree.android.wordle.presentation.extention

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.*
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.constant.Duration

fun fadeIn(vararg view: View) {
    view.forEach {
        it.fadeIn()
    }
}

fun View.fadeIn(
    duration: Long = Duration.Animation.FADE_IN,
    alphaFrom: Float = 0.0F,
    onAnimationEnd: (() -> Unit)? = null
): ViewPropertyAnimator? {
    if (isVisible) {
        onAnimationEnd?.invoke()

        return null
    }

    this.apply {
        alpha = alphaFrom
        visibility = View.VISIBLE

        return@fadeIn animate()
            .alpha(1.0F)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    onAnimationEnd?.invoke()
                }
            }).withLayer()
    }
}

fun fadeOut(vararg view: View) {
    view.forEach {
        it.fadeOut()
    }
}

fun View.fadeOut(
    duration: Long = Duration.Animation.FADE_OUT,
    invisible: Boolean = false,
    onAnimationEnd: (() -> Unit)? = null
): ViewPropertyAnimator? {
    if (isVisible.not()) {
        onAnimationEnd?.invoke()

        return null
    }

    this.apply {
        alpha = 1.0F

        return@fadeOut animate()
            .alpha(0.0F)
            .setDuration(duration)
            .setInterpolator(AccelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    this@fadeOut.visibility = if (invisible)
                        View.INVISIBLE
                    else
                        View.GONE

                    onAnimationEnd?.invoke()
                    super.onAnimationEnd(animation)
                }
            }).withLayer()
    }
}

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

fun View.scaleUpDown(from: Float = 1.0F, to: Float = 1.15F, duration: Long = Duration.Animation.SCALE_UP_DOWN) {
    scale(from, to, duration) {
        scale(to, from, duration)
    }
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

fun TextView.textFadeIn(text: CharSequence, duration: Long = Duration.Animation.FADE_IN) {
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

fun TextView.textFadeOut(duration: Long = Duration.Animation.FADE_OUT) {
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

fun RecyclerView.smoothSnapToPosition(position: Int, snapPreference: Int = LinearSmoothScroller.SNAP_TO_START) {
    val duration = 500.0F
    val linearSmoothScroller = object : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int = snapPreference

        override fun getHorizontalSnapPreference(): Int = snapPreference

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
            return duration / computeVerticalScrollRange()
        }
    }

    linearSmoothScroller.targetPosition = position

    layoutManager?.startSmoothScroll(linearSmoothScroller)
}