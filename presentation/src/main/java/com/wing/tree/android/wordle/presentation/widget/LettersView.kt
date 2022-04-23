package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.MainThread
import androidx.constraintlayout.widget.ConstraintLayout
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.LettersViewBinding
import com.wing.tree.android.wordle.presentation.extention.scale
import com.wing.tree.android.wordle.presentation.model.play.Letter
import kotlinx.coroutines.*

class LettersView : ConstraintLayout {
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate + job)
    private val viewBinding: LettersViewBinding = LettersViewBinding.bind(inflate(context, R.layout.letters_view, this))

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

    fun flip(@MainThread doOnEnd: (() -> Unit)? = null) {
        coroutineScope.launch {
            flip(0)
            flip(1)
            flip(2)
            flip(3)
            flip(4)

            delay(600L)
            doOnEnd?.invoke()
        }
    }

    fun flipAt(index: Int, @MainThread doOnEnd: ((LetterView) -> Unit)? = null) {
        get(index).flip(doOnEnd)
    }

    private suspend fun flip(index: Int) {
        with(get(index)) {
            if (flippable) {
                flip()
                delay(240L)
            }
        }
    }

    fun get(index: Int): LetterView = with(viewBinding) {
        when(index) {
            0 -> firstLetter
            1 -> secondLetter
            2 -> thirdLetter
            3 -> fourthLetter
            4 -> fifthLetter
            else -> throw IllegalArgumentException("$index")
        }
    }

    fun scaleAt(index: Int) {
        with(get(index)) {
            front.scale(1.0F, 1.25F, 240L) {
                front.scale(1.25F, 1.0F, 240L)
            }
        }
    }

    fun set(index: Int, letter: Letter) {
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

    fun setOnLetterClickListener(onLetterClickListener: OnLetterClickListener?) {
        this.onLetterClickListener = onLetterClickListener
    }
}