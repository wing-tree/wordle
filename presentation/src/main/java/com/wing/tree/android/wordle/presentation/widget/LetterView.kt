package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.LetterViewBinding
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.util.flip

class LetterView : FrameLayout, Flippable<LetterView> {
    private val viewBinding: LetterViewBinding = LetterViewBinding.bind(inflate(context, R.layout.letter_view, this))

    override var flippable = true
    override var isRunning = false

    var back = viewBinding.letterBack
    var front = viewBinding.letterFront

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
            if (flippable && isRunning.not()) {
                isRunning = true

                if (letterBack.isVisible) {
                    flip(letterFront, letterBack) {
                        back = letterBack
                        front = letterFront
                        doOnEnd?.invoke(this@LetterView)
                        isRunning = false
                    }
                } else {
                    flip(letterBack, letterFront) {
                        back = letterFront
                        front = letterBack
                        doOnEnd?.invoke(this@LetterView)
                        isRunning = false
                    }
                }
            }
        }
    }

    private fun setText(letter: Letter) {
        val text = letter.value.uppercase()

        with(viewBinding) {
            letterBack.setBackgroundColor(letter.state.color)
            letterBack.text = text
            letterFront.text = text
        }
    }
}