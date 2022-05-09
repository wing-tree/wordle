package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.LetterViewBinding
import com.wing.tree.android.wordle.presentation.extention.scale
import com.wing.tree.android.wordle.presentation.extention.textFadeIn
import com.wing.tree.android.wordle.presentation.extention.textFadeOut
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.util.flip

class LetterView : FrameLayout, Flippable<LetterView> {
    private val viewBinding: LetterViewBinding = LetterViewBinding.bind(inflate(context, R.layout.letter_view, this))
    private val isFlipped: Boolean get() = viewBinding.textViewBack.isVisible
    private val isNotAnimating: Boolean get() = isAnimating.not()

    override var isFlippable = true
    override var isAnimating = false

    private var backFrame = viewBinding.frameLayoutBack
    private var backText = viewBinding.textViewBack
    private var frontFrame = viewBinding.frameLayoutFront
    private var frontText = viewBinding.textViewFront

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

            flip(backFrame, frontFrame) {
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
                backText = textViewFront
                frontText = textViewBack
                backFrame = frameLayoutFront
                frontFrame = frameLayoutBack
            } else {
                backText = textViewBack
                frontText = textViewFront
                backFrame = frameLayoutBack
                frontFrame = frameLayoutFront
            }
        }
    }

    fun submitLetter(letter: Letter, flag: Flag) {
        val text = letter.value.uppercase()
        val tint = letter.getTint(context)

        when(flag) {
            is Flag.Default -> {
                backFrame.backgroundTintList = tint
                backText.text = text

                when(flag.action) {
                    Flag.Action.Add -> {
                        frontText.textFadeIn(text)

                        scale(1.0F, 1.15F, 150L) {
                            scale(1.15F, 1.0F, 150L)
                        }
                    }
                    Flag.Action.Nothing -> frontText.text = text
                    Flag.Action.Remove -> frontText.textFadeOut()
                }
            }
            is Flag.Restore -> {
                frontFrame.backgroundTintList = tint
                frontText.text = text
            }
            is Flag.Result -> {
                frontFrame.backgroundTintList = tint
                frontText.text = text
            }
            is Flag.Submit -> {
                backFrame.backgroundTintList = tint
                backText.text = text
            }
        }
    }

    sealed class Flag {
        data class Default(val action: Action) : Flag()
        object Restore : Flag()
        object Result : Flag()
        object Submit : Flag()

        enum class Action {
            Add, Nothing, Remove
        }
    }
}