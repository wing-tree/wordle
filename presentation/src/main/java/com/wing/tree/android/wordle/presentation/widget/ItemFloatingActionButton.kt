package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.ItemFloatingActionButtonBinding

class ItemFloatingActionButton : FrameLayout {
    private val viewBinding = ItemFloatingActionButtonBinding.bind(inflate(context, R.layout.item_floating_action_button, this))

    var count: Int = 0
        set(value) {
            field = value
            viewBinding.textViewCount.text = "$field"
        }

    var price: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        getAttrs(attrs)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemFloatingActionButton)

        val src = typedArray.getDrawable(R.styleable.ItemFloatingActionButton_src)

        viewBinding.floatingActionButton.setImageDrawable(src)

        typedArray.recycle()
    }

    override fun setOnClickListener(l: OnClickListener?) {
        viewBinding.floatingActionButton.setOnClickListener(l)
    }
}