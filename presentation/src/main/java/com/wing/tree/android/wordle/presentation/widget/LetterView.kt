package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.constant.Duration
import com.wing.tree.android.wordle.presentation.databinding.LetterViewBinding
import com.wing.tree.android.wordle.presentation.extention.scaleUpDown
import com.wing.tree.android.wordle.presentation.extention.textFadeIn
import com.wing.tree.android.wordle.presentation.extention.textFadeOut
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.util.flip
import java.util.concurrent.atomic.AtomicBoolean

class LetterView : FrameLayout, Flippable<LetterView> {
    private val viewBinding: LetterViewBinding = LetterViewBinding.bind(inflate(context, R.layout.letter_view, this))
    private val isFlipped: Boolean get() = viewBinding.frameLayoutBack.isVisible

    override var isFlippable = true
    override var isAnimating = false

    private var backFrame = viewBinding.frameLayoutBack
    private var backText = viewBinding.textViewBack
    private var frontFrame = viewBinding.frameLayoutFront
    private var frontText = viewBinding.textViewFront

    var isHighContrastMode = false
        set(value) {
            if (field == value) {
                return
            }

            field = value

            if (isFlipped) {
                return
            }

            val background = if (field) {
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.transition_letter_view_high_contrast
                )
            } else {
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.transition_letter_view
                )
            }

            if (frontFrame.background != background) {
                println("zionzion1111")
                frontFrame.background = background
            } else {
                println("zionzion2222")
            }
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun flip(doOnEnd: ((LetterView) -> Unit)?) {
        if (isAnimating.not() && isFlippable) {
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

        transitionDrawable?.startTransition(Duration.Animation.TRANSITION)
    }

    private fun swap() {
        with(viewBinding) {
            if (isFlipped) {
                backFrame = frameLayoutFront
                backText = textViewFront

                frontFrame = frameLayoutBack
                frontText = textViewBack
            } else {
                backFrame = frameLayoutBack
                backText = textViewBack

                frontFrame = frameLayoutFront
                frontText = textViewFront
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
                            scaleUpDown()
                        }
                    }
                    Flag.Action.Nothing -> {
                        frontFrame.backgroundTintList = tint
                        frontText.text = text
                    }
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