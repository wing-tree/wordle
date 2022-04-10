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

    fun flip(@MainThread onFlipped: () -> Unit) {
        with(viewBinding) {
            coroutineScope.launch {
                firstLetter.flip()
                delay(120L)
                secondLetter.flip()
                delay(120L)
                thirdLetter.flip()
                delay(120L)
                fourthLetter.flip()
                delay(120L)
                fifthLetter.flip()

                onFlipped.invoke()
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
            scale(1.25F, 240L) {
                scale(1.0F, 240L)
            }
        }
    }

    fun set(index: Int, letter: Letter) {
        with(viewBinding) {
            when(index) {
                0 -> firstLetter.set(letter)
                1 -> secondLetter.set(letter)
                2 -> thirdLetter.set(letter)
                3 -> fourthLetter.set(letter)
                4 -> fifthLetter.set(letter)
            }
        }
    }

    fun setOnLetterClickListener(onLetterClickListener: OnLetterClickListener?) {
        this.onLetterClickListener = onLetterClickListener
    }
}