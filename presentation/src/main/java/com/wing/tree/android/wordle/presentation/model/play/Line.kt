package com.wing.tree.android.wordle.presentation.model.play

import com.wing.tree.android.wordle.presentation.mapper.PlayStateMapper.toPresentationModel
import com.wing.tree.wordle.core.constant.BLANK
import com.wing.tree.wordle.core.constant.EMPTY
import com.wing.tree.wordle.core.constant.WORD_LENGTH
import com.wing.tree.android.wordle.domain.model.playstate.Line as DomainLine

data class Line(val round: Int) : Iterable<Letter> {
    private val isNotEmpty: Boolean
        get() = notBlankCount > 0

    private var _isSubmitted: Boolean = false
    val isSubmitted: Boolean get() = _isSubmitted

    private var _isFocused: Boolean = false
    val isFocused: Boolean get() = _isFocused

    val currentLetters: Array<Letter> = Array(WORD_LENGTH) { Letter(position = it) }
    val previousLetters: Array<Letter> = Array(WORD_LENGTH) { Letter(position = it) }
    val undefinedLetters: List<Letter> get() = currentLetters.filterWithState<Letter.State.Undefined>()

    val notBlankCount: Int
        get() = currentLetters.count { it.isNotBlank }

    val proximity: Int get() = currentLetters.sumOf { it.state.priority }

    val string: String
        get() = currentLetters.joinToString(EMPTY) { it.value }

    private fun backup() {
        currentLetters.copyInto(previousLetters)
    }

    operator fun get(index: Int) = currentLetters[index]

    operator fun set(index: Int, letter: Letter) {
        currentLetters[index] = letter
    }

    fun add(letter: String) {
        if (notBlankCount < WORD_LENGTH) {
            backup()

            val index = currentLetters.indexOfFirst { it.value.isBlank() }

            if (index in 0 until WORD_LENGTH) {
                currentLetters[index] = Letter(index, letter)
            }
        }
    }

    fun matches(letters: String) = letters == string

    fun removeAt(index: Int) {
        if (isNotEmpty) {
            if (index in 0 until WORD_LENGTH) {
                val letter = get(index)
                val submitted = letter.isSubmitted

                if (submitted.not()) {
                    backup()
                    set(index, Letter(index, BLANK))
                }
            }
        }
    }

    fun removeFocus() {
        _isFocused = false
    }

    fun removeLast() {
        if (isNotEmpty) {
            val index = currentLetters.indexOfLast { it.isSubmitted.not() && it.isNotBlank  }

            if (index in 0 until WORD_LENGTH) {
                backup()
                set(index, Letter(index, BLANK))
            }
        }
    }

    fun requestFocus() {
        _isFocused = true
    }

    fun submit() {
        _isSubmitted = true
    }

    fun submitLetter(letter: Letter) {
        backup()
        set(letter.position, letter.apply { submit() })
    }

    inline fun <reified R: Letter.State> filterWithState(): List<Letter> {
        return currentLetters.filterWithState<R>()
    }

    inline fun <reified R: Letter.State> Array<Letter>.filterWithState(): List<Letter> {
        return filter { it.state is R }
    }

    override fun iterator(): Iterator<Letter> {
        return object : Iterator<Letter> {
            private var index = 0

            override fun hasNext(): Boolean {
                return index <= currentLetters.lastIndex
            }

            override fun next(): Letter {
                return currentLetters[index++]
            }
        }
    }

    companion object {
        fun from(line: DomainLine): Line {
            return Line(line.round).apply {
                line.currentLetters.sortedBy { it.position }.forEachIndexed { index, letter ->
                    currentLetters[index] = letter.toPresentationModel()
                }

                line.previousLetters.sortedBy { it.position }.forEachIndexed { index, letter ->
                    previousLetters[index] = letter.toPresentationModel()
                }

                _isFocused = line.isFocused
                _isSubmitted = line.isSubmitted
            }
        }
    }
}