package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.MainThread
import androidx.constraintlayout.widget.ConstraintLayout
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.LineViewBinding
import com.wing.tree.android.wordle.presentation.extention.scale
import com.wing.tree.android.wordle.presentation.model.play.Letter
import kotlinx.coroutines.*

class LineView : ConstraintLayout {
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate + job)
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
        with(viewBinding) {
            firstLetter.setOnClickListener { onLetterClickListener?.onLetterViewClick(firstLetter, 0) }
            secondLetter.setOnClickListener { onLetterClickListener?.onLetterViewClick(secondLetter, 1) }
            thirdLetter.setOnClickListener { onLetterClickListener?.onLetterViewClick(thirdLetter, 2) }
            fourthLetter.setOnClickListener { onLetterClickListener?.onLetterViewClick(fourthLetter, 3) }
            fifthLetter.setOnClickListener { onLetterClickListener?.onLetterViewClick(fifthLetter, 4) }
        }
    }

    fun flip(skipAnimation: Boolean = false, @MainThread doOnEnd: (() -> Unit)? = null) {
        coroutineScope.launch {
            if (skipAnimation) {
                flip(0, true)
                flip(1, true)
                flip(2, true)
                flip(3, true)
                flip(4, true)
            } else {
                flip(0, false)
                flip(1, false)
                flip(2, false)
                flip(3, false)
                flip(4, false)

                delay(600L)
            }

            doOnEnd?.invoke()
        }
    }

    fun flipAt(index: Int, @MainThread doOnEnd: ((LetterView) -> Unit)? = null) {
        get(index).flip(false, doOnEnd)
    }

    private suspend fun flip(index: Int, skipAnimation: Boolean) {
        with(get(index)) {
            if (isFlippable) {
                flip(skipAnimation)

                if (skipAnimation.not()) {
                    delay(240L)
                }
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

    operator fun get(index: Int): LetterView = with(viewBinding) {
        when(index) {
            0 -> firstLetter
            1 -> secondLetter
            2 -> thirdLetter
            3 -> fourthLetter
            4 -> fifthLetter
            else -> throw IllegalArgumentException("$index")
        }
    }

    operator fun set(index: Int, letter: Letter) {
        with(viewBinding) {
            when(index) {
                0 -> firstLetter.letter = letter
                1 -> secondLetter.letter = letter
                2 -> thirdLetter.letter = letter
                3 -> fourthLetter.letter = letter
                4 -> fifthLetter.letter = letter
            }
        }
    }

    fun setBackLetterAt(index: Int, letter: Letter) {
        with(viewBinding) {
            when(index) {
                0 -> firstLetter.backLetter = letter
                1 -> secondLetter.backLetter = letter
                2 -> thirdLetter.backLetter = letter
                3 -> fourthLetter.backLetter = letter
                4 -> fifthLetter.backLetter = letter
            }
        }
    }

    fun setOnLetterClickListener(onLetterClickListener: OnLetterClickListener?) {
        this.onLetterClickListener = onLetterClickListener
    }
}