package com.wing.tree.android.wordle.presentation.model.play

import androidx.annotation.ColorRes
import com.wing.tree.android.wordle.domain.model.playstate.Key.Alphabet.State.MATCHED
import com.wing.tree.android.wordle.domain.model.playstate.Key.Alphabet.State.MISMATCHED
import com.wing.tree.android.wordle.domain.model.playstate.Key.Alphabet.State.NOT_IN
import com.wing.tree.android.wordle.domain.model.playstate.Key.Alphabet.State.UNDEFINED
import com.wing.tree.android.wordle.presentation.R

sealed class Key {
    data class Alphabet(val letter: String) : Key() {
        private var _state: State = State.Undefined()
        val state: State get() = _state

        fun erase() {
            _state = State.NotIn()
        }

        fun updateState(state: State) {
            if (this.state.priority < state.priority) {
                _state = state
            }
        }
    }

    object Backspace : Key()
    object Return : Key()

    sealed class State {
        @get:ColorRes
        abstract val backgroundColorRes: Int
        abstract val priority: Int
        @get:ColorRes
        abstract val textColorRes: Int

        val notUndefined: Boolean get() = this !is Undefined

        fun toInt() = when(this) {
            is Undefined -> UNDEFINED
            is NotIn -> NOT_IN
            is In.Mismatched -> MISMATCHED
            is In.Matched -> MATCHED
        }

        data class Undefined(
            override val backgroundColorRes: Int = R.color.undefined,
            override val priority: Int = Priority.UNDEFINED,
            override val textColorRes: Int = R.color.white
        ): State()

        data class NotIn(
            override val backgroundColorRes: Int = R.color.not_in,
            override val priority: Int = Priority.NOT_IN,
            override val textColorRes: Int = R.color.disabled
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
            const val MATCHED = 6
        }

        companion object {
            fun from(state: Letter.State) = when(state) {
                is Letter.State.NotIn -> NotIn()
                is Letter.State.In.Matched -> In.Matched()
                is Letter.State.In.Mismatched -> In.Mismatched()
                is Letter.State.Undefined -> Undefined()
            }

            fun fromInt(int: Int) = when(int) {
                UNDEFINED -> Undefined()
                NOT_IN -> NotIn()
                MISMATCHED -> In.Mismatched()
                MATCHED -> In.Matched()
                else -> throw IllegalArgumentException("$int")
            }
        }
    }
}