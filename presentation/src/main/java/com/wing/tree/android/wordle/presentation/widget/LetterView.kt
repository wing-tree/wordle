package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.LetterViewBinding
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.util.flip

class LetterView : FrameLayout, Flippable<LetterView> {
    private val viewBinding: LetterViewBinding = LetterViewBinding.bind(inflate(context, R.layout.letter_view, this))
    private val isFlipped: Boolean get() = viewBinding.textViewBack.isVisible

    override var isFlippable = true
    override var isAnimating = false

    private var back = viewBinding.textViewBack
    private var front = viewBinding.textViewFront

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun flip(doOnEnd: ((LetterView) -> Unit)?) {
        if (isFlippable && isAnimating.not()) {
            isAnimating = true

            flip(back, front) {
                swap()
                doOnEnd?.invoke(this)
                isAnimating = false
            }
        }
    }

    private fun swap() {
        with(viewBinding) {
            if (isFlipped) {
                back = textViewFront
                front = textViewBack
            } else {
                back = textViewBack
                front = textViewFront
            }
        }
    }

    fun submitLetter(letter: Letter, featureFlag: FeatureFlag) {
        val text = letter.value.uppercase()
        val tint = letter.getTint(context)

        when(featureFlag) {
            is FeatureFlag.Normal -> {
                front.text = text

                back.text = text
                back.backgroundTintList = tint
            }
            is FeatureFlag.Restore -> {
                front.text = text
                front.backgroundTintList = tint
            }
            is FeatureFlag.Result -> {
                front.text = text
                front.backgroundTintList = tint
            }
            is FeatureFlag.Submit -> {
                back.text = text
                back.backgroundTintList = tint
            }
        }
    }

    sealed class FeatureFlag {
        object Normal : FeatureFlag()
        object Restore : FeatureFlag()
        object Result : FeatureFlag()
        object Submit : FeatureFlag()
    }
}