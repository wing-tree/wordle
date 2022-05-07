package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.LetterViewBinding
import com.wing.tree.android.wordle.presentation.extention.gone
import com.wing.tree.android.wordle.presentation.extention.visible
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.util.flip
import com.wing.tree.wordle.core.constant.BLANK

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

    var backLetter: Letter? = null
        set(value) {
            field = value
            field?.let {
                back.text = it.value.uppercase()
                back.backgroundTintList = it.getTint(context)
            }
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun flip(skipAnimation: Boolean, doOnEnd: ((LetterView) -> Unit)?) {
        val letterView = this

        if (skipAnimation) {
            with(viewBinding) {
                if (isFlippable && isAnimating.not()) {
                    isAnimating = true

                    if (letterBack.isVisible) {
                        letterBack.gone()
                        letterFront.visible()
                        back = letterBack
                        front = letterFront
                        doOnEnd?.invoke(letterView)
                        isAnimating = false
                    } else {
                        letterBack.visible()
                        letterFront.gone()
                        back = letterFront
                        front = letterBack
                        doOnEnd?.invoke(letterView)
                        isAnimating = false
                    }
                }
            }
        } else {
            flip(doOnEnd)
        }
    }

    private fun flip(doOnEnd: ((LetterView) -> Unit)?) {
        val letterView = this

        with(viewBinding) {
            if (isFlippable && isAnimating.not()) {
                isAnimating = true

                if (letterBack.isVisible) {
                    flip(letterFront, letterBack) {
                        back = letterBack
                        front = letterFront
                        doOnEnd?.invoke(letterView)
                        isAnimating = false
                    }
                } else {
                    flip(letterBack, letterFront) {
                        back = letterFront
                        front = letterBack
                        doOnEnd?.invoke(letterView)
                        isAnimating = false
                    }
                }
            }
        }
    }

    private fun setText(letter: Letter) {
        val text = letter.value.uppercase()

        with(viewBinding) {
            back.backgroundTintList = letter.getTint(context)
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