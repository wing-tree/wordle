package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.KeyViewBinding
import com.wing.tree.android.wordle.presentation.extention.scale
import com.wing.tree.android.wordle.presentation.model.play.Key
import com.wing.tree.android.wordle.presentation.util.flip
import com.wing.tree.wordle.core.constant.BLANK

class KeyView : FrameLayout, Flippable<KeyView> {
    private val viewBinding: KeyViewBinding = KeyViewBinding.bind(inflate(context, R.layout.key_view, this))
    private val isFlipped: Boolean get() = viewBinding.textViewBack.isVisible
    private val isNotAnimating: Boolean get() = isAnimating.not()

    private var state: Key.State = Key.State.Undefined()

    override var isFlippable = true
    override var isAnimating = false

    private var back = viewBinding.textViewBack
    private var front = viewBinding.textViewFront

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        getAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        getAttrs(attrs, defStyleAttr)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KeyView)

        setTypedArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KeyView, defStyleAttr, 0)

        setTypedArray(typedArray)
    }

    private fun setTypedArray(typedArray: TypedArray) {
        val text = typedArray.getString(R.styleable.KeyView_key) ?: BLANK

        with(viewBinding) {
            textViewBack.text = text
            textViewFront.text = text
        }

        typedArray.recycle()
    }

    override fun flip(doOnEnd: ((KeyView)-> Unit)?) {
        if (isFlippable && isNotAnimating) {
            isAnimating = true
            isClickable = false

            flip(back, front) {
                swap()
                doOnEnd?.invoke(this)
                isAnimating = false
                isClickable = true
            }
        } else {
            doOnEnd?.invoke(this)
        }
    }

    fun updateState(state: Key.State, runsAnimation: Boolean = true) {
        if (this.state.priority < state.priority) {
            this.state = state

            val backgroundColor = context.getColor(state.backgroundColorRes)
            val textColor = context.getColor(state.textColorRes)
            val tint = ColorStateList.valueOf(backgroundColor)

            if (runsAnimation) {
                back.backgroundTintList = tint
                back.setTextColor(textColor)

                flip()
            } else {
                front.backgroundTintList = tint
                front.setTextColor(textColor)
            }
        }
    }

    fun scale() {
        scale(1.0F, 1.15F, 150L) {
            scale(1.15F, 1.0F, 150L)
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
}