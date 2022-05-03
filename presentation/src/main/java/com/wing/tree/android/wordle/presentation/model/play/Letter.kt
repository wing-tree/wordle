package com.wing.tree.android.wordle.presentation.model.play

import androidx.annotation.ColorRes
import com.wing.tree.android.wordle.android.constant.BLANK
import com.wing.tree.android.wordle.domain.model.playstate.Letter as DomainLetter
import com.wing.tree.android.wordle.presentation.R

data class Letter(val position: Int, val value: String = BLANK) {
    constructor(position: Int, value: Char): this(position, "$value")

    private var _isSubmitted: Boolean = false
    val isSubmitted: Boolean get() = _isSubmitted

    private var _state: State = State.Unknown()
    val state: State get() = _state

    val isBlank: Boolean
        get() = value.isBlank()

    val isNotBlank: Boolean
        get() = value.isNotBlank()

    fun updateState(state: State) {
        _state = state
    }

    fun submit() {
        _isSubmitted = true
        _state = State.In.Matched()
    }

    sealed class State {
        @get:ColorRes
        abstract val colorRes: Int
        abstract val priority: Int

        val notUnknown: Boolean get() = this !is Unknown

        fun fromInt(int: Int) = when(int) {
            0 -> Unknown()
            1 -> NotIn()
            2 -> In.Mismatched()
            3 -> In.Matched()
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
    }
}