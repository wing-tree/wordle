package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.MainThread
import androidx.constraintlayout.widget.ConstraintLayout
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.LineViewBinding
import com.wing.tree.android.wordle.presentation.extention.scale
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.wordle.core.constant.WORD_LENGTH
import kotlinx.coroutines.*

class LineView : ConstraintLayout {
    private val supervisorJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate + supervisorJob)
    private val viewBinding: LineViewBinding = LineViewBinding.bind(inflate(context, R.layout.line_view, this))

    private var onLetterClickListener: OnLetterClickListener? = null

    fun interface OnLetterClickListener {
        fun onLetterViewClick(letterView: LetterView, index: Int)
    }

    constructor(context: Context) : super(context) {
        bind()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        bind()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        bind()
    }

    private fun bind() {
       repeat(WORD_LENGTH) {
            with(get(it)) {
                setOnClickListener { _ ->
                    onLetterClickListener?.onLetterViewClick(this, it)
                }
            }
        }
    }

    operator fun get(index: Int): LetterView = with(viewBinding) {
        when(index) {
            0 -> letter1
            1 -> letter2
            2 -> letter3
            3 -> letter4
            4 -> letter5
            else -> throw IllegalArgumentException("$index")
        }
    }

    fun flipAll(@MainThread doOnEnd: (() -> Unit)? = null) {
        coroutineScope.launch {
            flipAt(0)
            delay(240L)
            flipAt(1)
            delay(240L)
            flipAt(2)
            delay(240L)
            flipAt(3)
            delay(240L)
            flipAt(4)
            delay(240L)

            delay(600L)
            doOnEnd?.invoke()
        }
    }

    fun flipAt(index: Int, @MainThread doOnEnd: ((LetterView) -> Unit)? = null) {
        with(get(index)) {
            if (isFlippable) {
                flip(doOnEnd)
            }
        }
    }

    fun scaleAt(index: Int) {
        with(get(index)) {
            scale(1.0F, 1.25F, 200L) {
                scale(1.25F, 1.0F, 200L)
            }
        }
    }

    fun setOnLetterClickListener(onLetterClickListener: OnLetterClickListener?) {
        this.onLetterClickListener = onLetterClickListener
    }

    fun submitLetters(letters: Array<Letter>, featureFlag: LetterView.FeatureFlag) {
        letters.forEachIndexed { index, letter ->
            get(index).submitLetter(letter, featureFlag)
        }
    }
}