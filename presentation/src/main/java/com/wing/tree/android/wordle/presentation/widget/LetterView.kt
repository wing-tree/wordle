package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.constant.Duration
import com.wing.tree.android.wordle.presentation.databinding.LetterViewBinding
import com.wing.tree.android.wordle.presentation.extention.scaleUpDown
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

    fun startTransition() {
        val transitionDrawable = frontFrame.background as? TransitionDrawable

        transitionDrawable?.isCrossFadeEnabled = true
        transitionDrawable?.startTransition(Duration.Animation.TRANSITION)
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
        val textColor = letter.getTextColor(context)
        val tint = letter.getTint(context)

        when(flag) {
            is Flag.Default -> {
                backFrame.backgroundTintList = tint
                backText.text = text
                backText.setTextColor(textColor)

                when(flag.action) {
                    Flag.Action.Add -> {
                        if (frontText.text.isBlank()) {
                            frontText.textFadeIn(text)
                            scaleUpDown(1.0F, 1.15F, Duration.Animation.SCALE_UP_DOWN)
                        }
                    }
                    Flag.Action.Nothing -> frontText.text = text
                    Flag.Action.Remove -> frontText.textFadeOut()
                }
            }
            is Flag.Restore -> {
                frontFrame.backgroundTintList = tint
                frontText.text = text
                frontText.setTextColor(textColor)
            }
            is Flag.Result -> {
                frontFrame.backgroundTintList = tint
                frontText.text = text
                frontText.setTextColor(textColor)
            }
            is Flag.Submit -> {
                backFrame.backgroundTintList = tint
                backText.text = text
                backText.setTextColor(textColor)
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