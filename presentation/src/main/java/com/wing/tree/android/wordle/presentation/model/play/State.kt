package com.wing.tree.android.wordle.presentation.model.play

import android.graphics.Color

sealed class State {
    abstract val color: Int
    abstract val priority: Int

    val notUnknown: Boolean get() = this !is Unknown

    sealed class Correct : State() {
        data class InRightPlace(
            override val color: Int = Color.GREEN,
            override val priority: Int = 3
        ) : Correct()

        data class InWrongPlace(
            override val color: Int = Color.MAGENTA,
            override val priority: Int = 2
        ) : Correct()
    }

    data class Incorrect(
        override val color: Int = Color.BLACK,
        override val priority: Int = 1
    ): State()

    data class Unknown(
        override val color: Int = Color.BLACK,
        override val priority: Int = 0
    ): State()
}