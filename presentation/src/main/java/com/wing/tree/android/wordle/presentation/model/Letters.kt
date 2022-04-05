package com.wing.tree.android.wordle.presentation.model

import com.wing.tree.android.wordle.android.constant.BLANK

data class Letters(
    var letters: String = BLANK,
    var previousLetters: String = BLANK
) {
    fun addLetter(letter: String) {
        previousLetters = letters
        letters += letter
    }

    fun removeLastLetter() {
        previousLetters = letters
        letters = letters.dropLast(1)
    }
}