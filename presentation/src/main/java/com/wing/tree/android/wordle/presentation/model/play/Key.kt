package com.wing.tree.android.wordle.presentation.model.play

import androidx.annotation.ColorRes
import com.wing.tree.android.wordle.domain.model.playstate.Key.Alphabet as DomainAlphabet
import com.wing.tree.android.wordle.presentation.R

sealed class Key {
    data class Alphabet(val letter: String) : Key() {
        private var _state: State = State.Unknown()
        val state: State get() = _state

        fun erase() {
            _state = State.NotIn()
        }

        fun updateState(state: Letter.State) {
            _state = State.from(state)
        }
    }

    object Backspace : Key()
    object Return : Key()

    sealed class State {
        @get:ColorRes
        abstract val colorRes: Int
        abstract val priority: Int

        val notUnknown: Boolean get() = this !is Unknown

        fun fromInt(int: Int) = when(int) {
            0 -> Letter.State.Unknown()
            1 -> Letter.State.NotIn()
            2 -> Letter.State.In.Mismatched()
            3 -> Letter.State.In.Matched()
            else -> throw IllegalArgumentException("$int")
        }

        fun toInt() = when(this) {
            is Unknown -> 0
            is NotIn -> 1
            is In.Mismatched -> 2
            is In.Matched -> 3
        }

        data class Unknown(
            override val colorRes: Int = R.color.dark_grey,
            override val priority: Int = Priority.UNKNOWN
        ): State()

        data class NotIn(
            override val colorRes: Int = R.color.black,
            override val priority: Int = Priority.NOT_IN
        ): State()

        sealed class In : State() {
            data class Mismatched(
                override val colorRes: Int = R.color.yellow_500,
                override val priority: Int = Priority.MISMATCHED
            ) : In()

            data class Matched(
                override val colorRes: Int = R.color.green_800,
                override val priority: Int = Priority.MATCHED
            ) : In()
        }

        private object Priority {
            const val UNKNOWN = 0
            const val NOT_IN = 1
            const val MISMATCHED = 2
            const val MATCHED = 3
        }

        companion object {
            fun from(state: Letter.State) = when(state) {
                is Letter.State.NotIn -> NotIn()
                is Letter.State.In.Matched -> In.Matched()
                is Letter.State.In.Mismatched -> In.Mismatched()
                is Letter.State.Unknown -> Unknown()
            }
        }
    }
}