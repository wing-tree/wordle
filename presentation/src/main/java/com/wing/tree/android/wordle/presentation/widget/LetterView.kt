package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.view.isVisible
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.LetterViewBinding
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.util.flip

class LetterView : FrameLayout, Flippable<LetterView> {
    private val viewBinding: LetterViewBinding = LetterViewBinding.bind(inflate(context, R.layout.letter_view, this))

    override var isFlippable = true
    override var isAnimating = false

    private var back = viewBinding.letterBack
    private var front = viewBinding.letterFront

    var letter: Letter? = null
        set(value) {
            field = value
            field?.let { setText(it) }
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun flip(doOnEnd: ((LetterView) -> Unit)?) {
        with(viewBinding) {
            if (isFlippable && isAnimating.not()) {
                isAnimating = true

                if (letterBack.isVisible) {
                    flip(letterFront, letterBack) {
                        back = letterBack
                        front = letterFront
                        doOnEnd?.invoke(this@LetterView)
                        isAnimating = false
                    }
                } else {
                    flip(letterBack, letterFront) {
                        back = letterFront
                        front = letterBack
                        doOnEnd?.invoke(this@LetterView)
                        isAnimating = false
                    }
                }
            }
        }
    }

    private fun setText(letter: Letter) {
        val text = letter.value.uppercase()

        with(viewBinding) {
            val color = context.getColor(letter.state.backgroundColorRes)

            back.backgroundTintList = ColorStateList.valueOf(color)
            letterBack.text = text
            letterFront.text = text
        }
    }

    fun setFrontText(text: String) {
        front.text = text
    }

    fun setFrontBackgroundColor(@ColorInt color: Int) {
        front.backgroundTintList = ColorStateList.valueOf(color)
    }
}