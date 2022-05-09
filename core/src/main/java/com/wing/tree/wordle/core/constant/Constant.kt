package com.wing.tree.wordle.core.constant

const val BLANK = " "
const val EMPTY = ""
const val MAXIMUM_ROUND = 6
const val WORD_LENGTH = 5

val alphabet = arrayOf(
    "a", "b", "c", "d",
    "e", "f", "g", "h",
    "i", "j", "k", "l",
    "m", "n", "o", "p",
    "q", "r", "s", "t",
    "u", "v", "w", "x",
    "y", "z"
)

val Int.isZero: Boolean get() = this == 0