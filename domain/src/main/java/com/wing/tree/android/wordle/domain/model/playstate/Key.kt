package com.wing.tree.android.wordle.domain.model.playstate

interface Key {
    interface Alphabet {
        val letter: String
        val state: Int

        object State {
            const val UNKNOWN = 0
            const val NOT_IN = 1
            const val MISMATCHED = 2
            const val MATCHED = 3
        }
    }
}