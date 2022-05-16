package com.wing.tree.android.wordle.domain.model.playstate

interface Line {
    val round: Int
    val currentLetters: List<Letter>
    val previousLetters: List<Letter>
    val isFocused: Boolean
    val isSubmitted: Boolean
}