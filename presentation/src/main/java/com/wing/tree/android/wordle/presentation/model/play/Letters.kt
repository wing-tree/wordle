package com.wing.tree.android.wordle.presentation.model.play

import com.wing.tree.android.wordle.android.constant.BLANK
import com.wing.tree.android.wordle.presentation.constant.Word.LENGTH

data class Letters(val letters: Array<Letter> = Array(LENGTH) { Letter() }) {
    val isNotEmpty: Boolean
        get() = length > 0

    val length: Int
        get() = letters.count { it.isNotBlank }

    val string: String
        get() = letters.joinToString(BLANK) { it.letter }

    var previousLetters: Array<Letter> = Array(LENGTH) { Letter() }
    var submitted: Boolean = false

    private fun backup() {
        previousLetters = Array(LENGTH) { letters[it] }
    }

    operator fun get(index: Int) = letters[index]

    inline fun <reified R: Letter.State> filterIsState(): List<Letter> {
        return letters.filter { it.state is R }
    }

    fun add(letter: String) {
        backup()

        if (length < LENGTH) {
            val index = letters.indexOfFirst { it.letter.isBlank() }

            if (index > -1) {
                letters[index] = Letter(letter)
            }
        }
    }

    fun matches(letters: String) = letters == string

    fun removeAt(index: Int) {
        backup()

        if (isNotEmpty) {
            if (index in 0 until LENGTH) {
                letters[index] = Letter(BLANK)
            }
        }
    }

    fun removeLast() {
        backup()

        if (isNotEmpty) {
            val index = letters.indexOfLast { it.letter.isNotBlank() }

            if (index > -1) {
                letters[index] = Letter(BLANK)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Letters) return false

        if (!letters.contentEquals(other.letters)) return false
        if (!previousLetters.contentEquals(other.previousLetters)) return false
        if (submitted != other.submitted) return false

        return true
    }

    override fun hashCode(): Int {
        var result = letters.contentHashCode()
        result = 31 * result + previousLetters.contentHashCode()
        result = 31 * result + submitted.hashCode()
        return result
    }
}