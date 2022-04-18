package com.wing.tree.android.wordle.presentation.model.play

import android.graphics.Color

sealed class State {
    abstract val color: Int
    abstract val priority: Int

    val notUnknown: Boolean get() = this !is Unknown

    sealed class In : State() {
        data class CorrectSpot(
            override val color: Int = Color.GREEN,
            override val priority: Int = Priority.CORRECT_SPOT
        ) : In()

        data class WrongSpot(
            override val color: Int = Color.MAGENTA,
            override val priority: Int = Priority.WRONG_SPOT
        ) : In()
    }

    data class NotIn(
        override val color: Int = Color.RED,
        override val priority: Int = Priority.NOT_IN
    ): State()

    data class Unknown(
        override val color: Int = Color.BLACK,
        override val priority: Int = Priority.UNKNOWN
    ): State()

    private object Priority {
        const val UNKNOWN = 0
        const val NOT_IN = 1
        const val WRONG_SPOT = 2
        const val CORRECT_SPOT = 3
    }
}