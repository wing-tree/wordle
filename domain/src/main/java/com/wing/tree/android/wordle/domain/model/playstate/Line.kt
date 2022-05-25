package com.wing.tree.android.wordle.domain.model.playstate

interface Line {
    val currentLetters: List<Letter>
    val isFocused: Boolean
    val isSubmitted: Boolean
    val previousLetters: List<Letter>
    val round: Int
}