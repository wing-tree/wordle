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

    val letters: Array<Letter> = Array(WORD_LENGTH) { Letter(position = it) }
    val previousLetters: Array<Letter> = Array(WORD_LENGTH) { Letter(position = it) }
    val unknownLetters: List<Letter> get() = letters.filterWithState<Letter.State.Unknown>()

    val notBlankCount: Int
        get() = letters.count { it.isNotBlank }

    val string: String
        get() = letters.joinToString(EMPTY) { it.value }

    private fun backup() {
        letters.copyInto(previousLetters)
    }

    operator fun get(index: Int) = letters[index]

    operator fun set(index: Int, letter: Letter) {
        letters[index] = letter
    }

    fun add(letter: String) {
        if (notBlankCount < WORD_LENGTH) {
            backup()

            val index = letters.indexOfFirst { it.value.isBlank() }

            if (index in 0 until WORD_LENGTH) {
                letters[index] = Letter(index, letter)
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

    fun removeLast() {
        if (isNotEmpty) {
            val index = letters.indexOfLast { it.isSubmitted.not() && it.isNotBlank  }

            if (index in 0 until WORD_LENGTH) {
                backup()
                set(index, Letter(index, BLANK))
            }
        }
    }

    fun submit() {
        _isSubmitted = true
    }

    fun submit(letter: Letter) {
        backup()
        set(letter.position, letter.apply { submit() })
    }

    inline fun <reified R: Letter.State> filterWithState(): List<Letter> {
        return letters.filterWithState<R>()
    }

    inline fun <reified R: Letter.State> Array<Letter>.filterWithState(): List<Letter> {
        return filter { it.state is R }
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

    companion object {
        fun from(line: DomainLine): Line {
            return Line(line.round).apply {
                line.letters.sortedBy { it.position }.forEachIndexed { index, letter ->
                    letters[index] = letter.toPresentationModel()
                }

                line.previousLetters.sortedBy { it.position }.forEachIndexed { index, letter ->
                    previousLetters[index] = letter.toPresentationModel()
                }

                _isSubmitted = line.isSubmitted
            }
        }
    }
}