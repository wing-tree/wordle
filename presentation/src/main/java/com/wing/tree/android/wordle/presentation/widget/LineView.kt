package com.wing.tree.android.wordle.presentation.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.MainThread
import androidx.constraintlayout.widget.ConstraintLayout
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.LineViewBinding
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.wordle.core.constant.WORD_LENGTH
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

class LineView : ConstraintLayout {
    private val supervisorJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate + supervisorJob)
    private val viewBinding: LineViewBinding = LineViewBinding.bind(inflate(context, R.layout.line_view, this))

    private val lastIndex = WORD_LENGTH.dec()

    private var isFocused = AtomicBoolean(false)
    private var onLetterClickListener: OnLetterClickListener? = null

    fun requestFocus(isFocused: Boolean, letters: Array<Letter>) {
        if (this.isFocused.compareAndSet(isFocused.not(), isFocused)) {
            letters.forEach {
                if (it.isSubmitted.not()) {
                    get(it.position).startTransition()
                }
            }
        }
    }

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
            0 -> letterView1
            1 -> letterView2
            2 -> letterView3
            3 -> letterView4
            4 -> letterView5
            else -> throw IndexOutOfBoundsException("$index")
        }
    }

    fun flipAll(@MainThread doOnEnd: (() -> Unit)? = null) {
        var lastFlippableIndex = lastIndex

        for (index in WORD_LENGTH.dec() downTo 0) {
            if (get(index).isFlippable) {
                lastFlippableIndex = index
                break
            }
        }

        coroutineScope.launch {
            for (index in 0..lastFlippableIndex) {
                with(get(index)) {
                    if (isFlippable) {
                        flip {
                            if (index == lastFlippableIndex) {
                                doOnEnd?.invoke()
                            }
                        }

                        delay(150L)
                    } else {
                        if (index == lastFlippableIndex) {
                            doOnEnd?.invoke()
                        }
                    }
                }
            }
        }
    }

    fun flipAt(index: Int, @MainThread doOnEnd: ((LetterView) -> Unit)? = null) {
        get(index).flip(doOnEnd)
    }

    fun setOnLetterClickListener(onLetterClickListener: OnLetterClickListener?) {
        this.onLetterClickListener = onLetterClickListener
    }

    fun submitLetters(letters: Array<Letter>, flag: LetterView.Flag) {
        letters.forEachIndexed { index, letter ->
            get(index).submitLetter(letter, flag)
        }
    }
}