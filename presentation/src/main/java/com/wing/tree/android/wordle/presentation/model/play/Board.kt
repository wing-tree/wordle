package com.wing.tree.android.wordle.presentation.model.play

import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.presentation.constant.Attempt
import com.wing.tree.android.wordle.presentation.constant.Word.LENGTH
import com.wing.tree.android.wordle.presentation.util.increment
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class Board {
    private val attempt = AtomicInteger(0)
    private val maximumAttempt = AtomicInteger(Attempt.MAXIMUM)

    val attemptExceeded: Boolean get() = attempt.get() >= maximumAttempt.get().dec()
    val attemptIncremented = AtomicBoolean(false)

    private val _lines = mutableListOf<Line>()
    val lines: List<Line> get() = _lines

    val letters = lines.flatten()

    val currentLine: Line get() = lines[attempt.get()]

    val lettersMatched = lettersWithState(State.Included.Matched())

    private fun lettersWithState(vararg state: State) = letters.filter { state.contains(it.state) }

    fun getNotMatchedYetLetters(word: Word): List<Letter> {
        val matchedPositions = lettersMatched.map { it.position }

        return mutableListOf<Letter>().apply {
            word.word.forEachIndexed { index, letter ->
                if (matchedPositions.contains(index).not()) {
                    add(Letter(index, letter))
                }
            }
        }
    }

    val notUnknownLetters: List<Letter> get() = letters.filter { it.state.notUnknown }

    init {
        repeat(Attempt.MAXIMUM) {
            _lines.add(Line())
        }
    }

    fun add(letter: String) {
        with(currentLine) {
            if (length < LENGTH) {
                add(letter)
            }
        }
    }

    fun removeAt(attempt: Int, index: Int) {
        try {
            lines[attempt].removeAt(index)
        } catch (e: ArrayIndexOutOfBoundsException) {
            Timber.e(e)
        }
    }

    fun removeLast() {
        currentLine.removeLast()
    }

    fun submit() {
        currentLine.submit()
    }

    fun addAttempt() {
        if (attemptIncremented.compareAndSet(false, true)) {
            maximumAttempt.increment()
            incrementAttempt()
            _lines.add(Line())
        }
    }

    fun incrementAttempt() {
        attempt.increment()
    }
}