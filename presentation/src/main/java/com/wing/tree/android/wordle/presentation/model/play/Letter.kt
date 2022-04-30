package com.wing.tree.android.wordle.presentation.model.play

import androidx.annotation.ColorRes
import com.wing.tree.android.wordle.android.constant.BLANK
import com.wing.tree.android.wordle.presentation.R

data class Letter(val position: Int, val value: String = BLANK, var state: State = State.Unknown()) {
    constructor(position: Int, value: Char): this(position, "$value")

    val isBlank: Boolean
        get() = value.isBlank()

    val isNotBlank: Boolean
        get() = value.isNotBlank()

    var submitted: Boolean = false

    sealed class State {
        @get:ColorRes
        abstract val colorRes: Int
        abstract val priority: Int

        val notUnknown: Boolean get() = this !is Unknown

        sealed class Included : State() {
            data class Matched(
                override val colorRes: Int = R.color.green_800,
                override val priority: Int = Priority.MATCHED
            ) : Included()

            data class NotMatched(
                override val colorRes: Int = R.color.yellow_500,
                override val priority: Int = Priority.NOT_MATCHED
            ) : Included()
        }

        data class Excluded(
            override val colorRes: Int = R.color.black,
            override val priority: Int = Priority.EXCLUDED
        ): State()

        data class Unknown(
            override val colorRes: Int = R.color.dark_grey,
            override val priority: Int = Priority.UNKNOWN
        ): State()

        private object Priority {
            const val UNKNOWN = 0
            const val EXCLUDED = 1
            const val NOT_MATCHED = 2
            const val MATCHED = 3
        }
    }
}