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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class LineView : ConstraintLayout {
    private val supervisorJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate + supervisorJob)
    private val viewBinding: LineViewBinding = LineViewBinding.bind(inflate(context, R.layout.line_view, this))

    private val lastIndex = WORD_LENGTH.dec()

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

                        delay(200)
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

    fun scaleAt(index: Int) {
        with(get(index)) {
            scale(1.0F, 1.15F, 200L) {
                scale(1.15F, 1.0F, 200L)
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