package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.wing.tree.android.wordle.android.constant.BLANK
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.KeyViewBinding
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.util.flip

class KeyView : FrameLayout, Flippable<KeyView> {
    private val viewBinding: KeyViewBinding = KeyViewBinding.bind(inflate(context, R.layout.key_view, this))

    private var state: Letter.State = Letter.State.Unknown()

    override var isFlippable = true
    override var isAnimating = false

    var back = viewBinding.keyBack
    var front = viewBinding.keyFront

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
            keyBack.text = text
            keyFront.text = text
        }

        typedArray.recycle()
    }

    override fun flip(doOnEnd: ((KeyView)-> Unit)?) {
        with(viewBinding) {
            if (isAnimating.not()) {
                isAnimating = true
                root.isClickable = false

                if (keyBack.isVisible) {
                    flip(keyFront, keyBack) {
                        back = keyBack
                        front = keyFront
                        doOnEnd?.invoke(this@KeyView)
                        isAnimating = false
                    }
                } else {
                    flip(keyBack, keyFront) {
                        back = keyFront
                        front = keyBack
                        doOnEnd?.invoke(this@KeyView)
                        isAnimating = false
                    }
                }
            }
        }
    }

    fun updateState(state: Letter.State) {
        if (this.state.priority < state.priority) {
            this.state = state

            val color = context.getColor(state.colorRes)

            back.backgroundTintList = ColorStateList.valueOf(color)

            flip {
                viewBinding.root.isClickable = true
            }
        }
    }
}