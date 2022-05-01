package com.wing.tree.android.wordle.presentation.model.play

import com.wing.tree.android.wordle.android.constant.BLANK
import com.wing.tree.android.wordle.presentation.constant.Word.LENGTH

data class Line(val number: Int) : Iterable<Letter> {
    private val isNotEmpty: Boolean
        get() = notBlankCount > 0

    val letters: Array<Letter> = Array(LENGTH) { Letter(position = it) }

    val notBlankCount: Int
        get() = letters.count { it.isNotBlank }

    val string: String
        get() = letters.joinToString(BLANK) { it.value }

    val previousLetters: Array<Letter> = Array(LENGTH) { Letter(position = it) }
    var submitted: Boolean = false

    private fun backup() {
        letters.copyInto(previousLetters)
    }

    operator fun get(index: Int) = letters[index]

    operator fun set(index: Int, letter: Letter) {
        letters[index] = letter
    }

    inline fun <reified R: Letter.State> filterWithState(): List<Letter> {
        return letters.filterWithState<R>()
    }

    inline fun <reified R: Letter.State> Array<Letter>.filterWithState(): List<Letter> {
        return filter { it.state is R }
    }

    fun add(letter: String) {
        if (notBlankCount < LENGTH) {
            backup()

            val index = letters.indexOfFirst { it.value.isBlank() }

            if (index in 0 until LENGTH) {
                letters[index] = Letter(index, letter)
            }
        }
    }

    fun matches(letters: String) = letters == string

    fun removeAt(index: Int) {
        if (isNotEmpty) {
            if (index in 0 until LENGTH) {
                val letter = get(index)
                val submitted = letter.submitted

                if (submitted.not()) {
                    backup()
                    set(index, Letter(index, BLANK))
                }
            }
        }
    }

    fun removeLast() {
        if (isNotEmpty) {
            val index = letters.indexOfLast { it.submitted.not() && it.isNotBlank  }

            if (index in 0 until LENGTH) {
                backup()
                set(index, Letter(index, BLANK))
            }
        }
    }

    fun submit() {
        submitted = true
    }

    override fun iterator(): Iterator<Letter> {
        return object : Iterator<Letter> {
            private var index = 0

            override fun hasNext(): Boolean {
                return index <= letters.lastIndex
            }

            override fun next(): Letter {
                return letters[index++]
            }
        }
    }
}