package com.wing.tree.android.wordle.presentation.model.play

import com.wing.tree.android.wordle.android.constant.BLANK

data class Letter(val position: Int, val value: String = BLANK) {
    constructor(position: Int, value: Char): this(position, "$value")

    val isBlank: Boolean
        get() = value.isBlank()

    val isNotBlank: Boolean
        get() = value.isNotBlank()

    var state: State = State.Unknown()
    var submitted: Boolean = false
}