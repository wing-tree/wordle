package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.FrameLayout
import com.wing.tree.android.wordle.android.constant.BLANK
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.KeyViewBinding

class KeyView : FrameLayout {
    private val viewBinding: KeyViewBinding = KeyViewBinding.bind(inflate(context, R.layout.key_view, this))

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

        viewBinding.keyBack.text = text
        viewBinding.keyFront.text = text

        typedArray.recycle()
    }
}