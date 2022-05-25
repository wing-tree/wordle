package com.wing.tree.android.wordle.domain.model.playstate

interface Letter {
    val isSubmitted: Boolean
    val position: Int
    val value: String
    val state: Int

    object State {
        const val UNDEFINED = 0
        const val NOT_IN = 1
        const val MISMATCHED = 2
        const val MATCHED = 3
    }
}