package com.wing.tree.android.wordle.domain.model.playstate

interface Line {
    val round: Int
    val letters: List<Letter>
    val previousLetters: List<Letter>
    val isSubmitted: Boolean
}