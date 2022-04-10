package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.LetterViewBinding
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.util.flip

class LetterView : FrameLayout {
    private val viewBinding: LetterViewBinding = LetterViewBinding.bind(inflate(context, R.layout.letter_view, this))

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun flip() {
        with(viewBinding) {
            flip(letterBack, letterFront)
        }
    }

    fun set(letter: Letter) {
        val text = letter.letter.uppercase()

        with(viewBinding) {
            letterBack.setBackgroundColor(letter.state.color)
            letterBack.text = text
            letterFront.text = text
        }
    }
}