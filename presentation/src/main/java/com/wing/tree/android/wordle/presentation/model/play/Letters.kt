package com.wing.tree.android.wordle.presentation.model.play

import com.wing.tree.android.wordle.android.constant.BLANK
import com.wing.tree.android.wordle.presentation.constant.Word.LENGTH

data class Letters(val value: Array<Letter> = Array(LENGTH) { Letter(position = it) }) : Iterable<Letter> {
    private val isNotEmpty: Boolean
        get() = length > 0

    val length: Int
        get() = value.count { it.isNotBlank }

    val string: String
        get() = value.joinToString(BLANK) { it.value }

    var previousLetters: Array<Letter> = Array(LENGTH) { Letter(position = it) }
    var submitted: Boolean = false

    private fun backup() {
        previousLetters = Array(LENGTH) { value[it] }
    }

    operator fun get(index: Int) = value[index]

    operator fun set(index: Int, letter: Letter) {
        value[index] = letter
    }

    inline fun <reified R: State> filterIsState(): List<Letter> {
        return value.filterIsState<R>()
    }

    inline fun <reified R: State> Array<Letter>.filterIsState(): List<Letter> {
        return this.filter { it.state is R }
    }

    fun add(letter: String) {
        if (length < LENGTH) {
            backup()

            val index = value.indexOfFirst { it.value.isBlank() }

            if (index in 0 until LENGTH) {
                value[index] = Letter(index, letter)
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
            val index = value.indexOfLast { it.submitted.not() && it.isNotBlank  }

            if (index in 0 until LENGTH) {
                backup()
                set(index, Letter(index, BLANK))
            }
        }
    }

    fun submit() {
        submitted = true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Letters) return false

        if (!value.contentEquals(other.value)) return false
        if (!previousLetters.contentEquals(other.previousLetters)) return false
        if (submitted != other.submitted) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.contentHashCode()
        result = 31 * result + previousLetters.contentHashCode()
        result = 31 * result + submitted.hashCode()
        return result
    }

    override fun iterator(): Iterator<Letter> {
        return object : Iterator<Letter> {
            private var index = 0

            override fun hasNext(): Boolean {
                return index <= value.lastIndex
            }

            override fun next(): Letter {
                return value[index++]
            }
        }
    }
}