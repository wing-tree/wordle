package com.wing.tree.android.wordle.domain.model.playstate

interface Letter {
    val position: Int
    val value: String
    val state: Int
    val isSubmitted: Boolean

    object State {
        const val UNKNOWN = 0
        const val NOT_IN = 1
        const val MISMATCHED = 2
        const val MATCHED = 3
    }
}