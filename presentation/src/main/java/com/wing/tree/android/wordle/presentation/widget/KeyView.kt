package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import com.wing.tree.android.wordle.android.constant.BLANK
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.KeyViewBinding
import com.wing.tree.android.wordle.presentation.model.play.State
import com.wing.tree.android.wordle.presentation.util.flip

class KeyView : FrameLayout {
    private val viewBinding: KeyViewBinding = KeyViewBinding.bind(inflate(context, R.layout.key_view, this))

    private var back: TextView = viewBinding.keyBack
    private var state: State = State.Unknown()

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

    private fun flip() {
        with(viewBinding) {
            root.isClickable = false

            if (keyBack.isVisible) {
                flip(keyFront, keyBack) {
                    back = keyBack
                    root.isClickable = true
                }
            } else {
                flip(keyBack, keyFront) {
                    back = keyFront
                    root.isClickable = true
                }
            }
        }
    }

    fun updateState(state: State) {
        if (this.state.priority < state.priority) {
            this.state = state

            back.setBackgroundColor(state.color)
            flip()
        }
    }
}