package com.wing.tree.android.wordle.presentation.model.play

import com.wing.tree.android.wordle.presentation.util.alphabet

class KeyBoard {
    val alphabetKeys = Array(alphabet.size) { Key.Alphabet(alphabet[it]) }
}