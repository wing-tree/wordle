package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.LetterViewBinding
import com.wing.tree.android.wordle.presentation.extention.gone
import com.wing.tree.android.wordle.presentation.extention.visible
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.util.flip

class LetterView : FrameLayout, Flippable<LetterView> {
    private val viewBinding: LetterViewBinding = LetterViewBinding.bind(inflate(context, R.layout.letter_view, this))

    override var isFlippable = true
    override var isAnimating = false

    private var back = viewBinding.letterBack

    private var _front = viewBinding.letterFront
    val front: TextView get() = _front

    @ColorInt
    var backBackgroundColor: Int = 0
        set(value) {
            field = value
            back.backgroundTintList = ColorStateList.valueOf(field)
        }

    @ColorInt
    var frontBackgroundColor: Int = 0
        set(value) {
            field = value
            front.backgroundTintList = ColorStateList.valueOf(field)
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun flip(doOnEnd: ((LetterView) -> Unit)?) {
        val letterView = this

        with(viewBinding) {
            if (isFlippable && isAnimating.not()) {
                isAnimating = true

                if (letterBack.isVisible) {
                    flip(letterFront, letterBack) {
                        back = letterBack
                        _front = letterFront
                        doOnEnd?.invoke(letterView)
                        isAnimating = false
                    }
                } else {
                    flip(letterBack, letterFront) {
                        back = letterFront
                        _front = letterBack
                        doOnEnd?.invoke(letterView)
                        isAnimating = false
                    }
                }
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