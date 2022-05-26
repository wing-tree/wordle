package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.ItemConsumptionFloatingActionButtonBinding
import com.wing.tree.android.wordle.presentation.extention.fadeIn
import com.wing.tree.android.wordle.presentation.extention.fadeOut
import com.wing.tree.android.wordle.presentation.extention.gone
import com.wing.tree.android.wordle.presentation.extention.visible
import com.wing.tree.wordle.core.util.isZero

class ItemConsumptionFloatingActionButton : FrameLayout {
    private val viewBinding = ItemConsumptionFloatingActionButtonBinding.bind(inflate(context, R.layout.item_consumption_floating_action_button, this))

    var count: Int = 0
        set(value) {
            field = value

            with(viewBinding) {
                if (field.isZero) {
                    linearLayoutCredits.fadeIn()
                    rollingTextViewCount.fadeOut()
                } else {
                    linearLayoutCredits.fadeOut()
                    rollingTextViewCount.fadeIn()
                    rollingTextViewCount.setText("$field")
                }
            }
        }

    var credits: Int = 0
        set(value) {
            field = value

            viewBinding.textViewCredits.text = "$field"
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        getAttrs(attrs)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemConsumptionFloatingActionButton)

        val src = typedArray.getDrawable(R.styleable.ItemConsumptionFloatingActionButton_src)
        val tint = typedArray.getColorStateList(R.styleable.ItemConsumptionFloatingActionButton_tint)
        val tintDark = typedArray.getColorStateList(R.styleable.ItemConsumptionFloatingActionButton_tint_dark)

        with(viewBinding) {
            floatingActionButton.backgroundTintList = tint
            floatingActionButton.setImageDrawable(src)
            linearLayoutCredits.backgroundTintList = tintDark
            rollingTextViewCount.animationDuration = 240L
        }
        typedArray.recycle()
    }

    override fun setOnClickListener(l: OnClickListener?) {
        viewBinding.floatingActionButton.setOnClickListener(l)
    }
}