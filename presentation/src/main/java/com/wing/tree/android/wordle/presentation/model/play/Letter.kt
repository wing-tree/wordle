package com.wing.tree.android.wordle.presentation.model.play

import android.graphics.Color
import com.wing.tree.android.wordle.android.constant.BLANK

data class Letter(
    val letter: String = BLANK,
    var state: State = State.Unknown()
) {
    val isBlank: Boolean
        get() = letter.isBlank()

    val isNotBlank: Boolean
        get() = letter.isNotBlank()

    sealed class State {
        abstract val color: Int

        data class Incorrect(override val color: Int = Color.BLACK): State()
        data class Unknown(override val color: Int = Color.BLACK): State()

        sealed class Correct : State() {
            data class InRightPlace(override val color: Int = Color.GREEN) : Correct()
            data class InWrongPlace(override val color: Int = Color.YELLOW) : Correct()
        }

    }
}