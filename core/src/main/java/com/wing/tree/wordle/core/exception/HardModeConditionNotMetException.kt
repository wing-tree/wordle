package com.wing.tree.wordle.core.exception

sealed class HardModeConditionNotMetException : Exception() {
    abstract val letter: String

    data class Matched(
        override val letter: String,
        val position: Int
    ) : HardModeConditionNotMetException()

    data class Mismatched(
        override val letter: String
    ) : HardModeConditionNotMetException()
}