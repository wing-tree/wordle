package com.wing.tree.android.wordle.android.exception


class WordNotFoundException(private val word: String) : Exception() {
    override val message: String
        get() = word
}