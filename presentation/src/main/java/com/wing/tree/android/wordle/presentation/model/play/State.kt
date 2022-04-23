package com.wing.tree.android.wordle.presentation.model.play

import android.graphics.Color

sealed class State {
    abstract val color: Int
    abstract val priority: Int

    val notUnknown: Boolean get() = this !is Unknown

    sealed class Included : State() {
        data class Matched(
            override val color: Int = Color.GREEN,
            override val priority: Int = Priority.MATCHED
        ) : Included()

        data class NotMatched(
            override val color: Int = Color.MAGENTA,
            override val priority: Int = Priority.NOT_MATCHED
        ) : Included()
    }

    data class Excluded(
        override val color: Int = Color.RED,
        override val priority: Int = Priority.EXCLUDED
    ): State()

    data class Unknown(
        override val color: Int = Color.BLACK,
        override val priority: Int = Priority.UNKNOWN
    ): State()

    private object Priority {
        const val UNKNOWN = 0
        const val EXCLUDED = 1
        const val NOT_MATCHED = 2
        const val MATCHED = 3
    }
}