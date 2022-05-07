package com.wing.tree.android.wordle.presentation.model.play

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.ColorRes
import com.wing.tree.android.wordle.domain.model.playstate.Letter
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.wordle.core.constant.BLANK

data class Letter(val position: Int, val value: String = BLANK) {
    constructor(position: Int, value: Char): this(position, "$value")

    private var _isSubmitted: Boolean = false
    val isSubmitted: Boolean get() = _isSubmitted

    private var _state: State = State.Undefined()
    val state: State get() = _state

    val isBlank: Boolean
        get() = value.isBlank()

    val isNotBlank: Boolean
        get() = value.isNotBlank()

    fun getTint(context: Context): ColorStateList {
        val color = context.getColor(state.backgroundColorRes)

        return ColorStateList.valueOf(color)
    }

    fun updateState(state: State) {
        _state = state
    }

    fun submit() {
        _isSubmitted = true
        _state = State.In.Matched()
    }

    sealed class State {
        @get:ColorRes
        abstract val backgroundColorRes: Int
        abstract val priority: Int
        @get:ColorRes
        abstract val textColorRes: Int

        val notUndefined: Boolean get() = this !is Undefined

        fun toInt() = when(this) {
            is Undefined -> Letter.State.UNDEFINED
            is NotIn -> Letter.State.NOT_IN
            is In.Mismatched -> Letter.State.MISMATCHED
            is In.Matched -> Letter.State.MATCHED
        }

        data class Undefined(
            override val backgroundColorRes: Int = R.color.undefined,
            override val priority: Int = Priority.UNDEFINED,
            override val textColorRes: Int = R.color.white
        ): State()

        data class NotIn(
            override val backgroundColorRes: Int = R.color.not_in,
            override val priority: Int = Priority.NOT_IN,
            override val textColorRes: Int = R.color.white
        ): State()

        sealed class In : State() {
            data class Mismatched(
                override val backgroundColorRes: Int = R.color.mismatched,
                override val priority: Int = Priority.MISMATCHED,
                override val textColorRes: Int = R.color.black
            ) : In()

            data class Matched(
                override val backgroundColorRes: Int = R.color.matched,
                override val priority: Int = Priority.MATCHED,
                override val textColorRes: Int = R.color.white
            ) : In()
        }

        private object Priority {
            const val UNDEFINED = 0
            const val NOT_IN = 1
            const val MISMATCHED = 2
            const val MATCHED = 4
        }

        companion object {
            fun fromInt(int: Int) = when(int) {
                Letter.State.UNDEFINED -> Undefined()
                Letter.State.NOT_IN -> NotIn()
                Letter.State.MISMATCHED -> In.Mismatched()
                Letter.State.MATCHED -> In.Matched()
                else -> throw IllegalArgumentException("$int")
            }
        }
    }
}