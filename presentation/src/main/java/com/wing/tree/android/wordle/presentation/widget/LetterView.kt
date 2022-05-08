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
    private val isNotAnimating: Boolean get() = isAnimating.not()

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
        if (isFlippable && isNotAnimating) {
            isAnimating = true

            flip(back, front) {
                swap()
                doOnEnd?.invoke(this)
                isAnimating = false
            }
        } else {
            doOnEnd?.invoke(this)
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

    fun submitLetter(letter: Letter, flag: Flag) {
        val text = letter.value.uppercase()
        val tint = letter.getTint(context)

        when(flag) {
            is Flag.Normal -> {
                back.text = text
                back.backgroundTintList = tint

                front.text = text
            }
            is Flag.Restore -> {
                front.text = text
                front.backgroundTintList = tint
            }
            is Flag.Result -> {
                front.text = text
                front.backgroundTintList = tint
            }
            is Flag.Submit -> {
                back.text = text
                back.backgroundTintList = tint
            }
        }
    }

    sealed class Flag {
        object Normal : Flag()
        object Restore : Flag()
        object Result : Flag()
        object Submit : Flag()
    }
}