package com.wing.tree.android.wordle.presentation.model.play

import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.presentation.constant.Attempt
import com.wing.tree.android.wordle.presentation.constant.Word.LENGTH
import com.wing.tree.android.wordle.presentation.util.alphabet
import com.wing.tree.android.wordle.presentation.util.increment
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class Board {
    private val _attempt = AtomicInteger(0)
    val attempt: Int get() = _attempt.get()

    private val maximumAttempt = AtomicInteger(Attempt.MAXIMUM)

    val attemptExceeded: Boolean get() = attempt >= maximumAttempt.get().dec()
    val attemptIncremented = AtomicBoolean(false)

    private val _lines = mutableListOf<Line>()
    val lines: List<Line> get() = _lines

    val letters get() = lines.flatten()
    val lettersExcluded = mutableListOf<Letter>()

    val currentLine: Line get() = lines[_attempt.get()]

    fun getNotMatchedYetLetters(word: Word): List<Letter> {
        val matchedPositions = letters.filterWithState<Letter.State.Included.Matched>().map { it.position }.distinct()

        return mutableListOf<Letter>().apply {
            word.value.forEachIndexed { index, letter ->
                if (matchedPositions.contains(index).not()) {
                    add(Letter(index, letter))
                }
            }
        }
    }

    val notUnknownLetters: List<Letter> get() = letters.filter { it.state.notUnknown }

    init {
        repeat(Attempt.MAXIMUM) {
            _lines.add(Line(it))
        }
    }

    fun add(letter: String) {
        with(currentLine) {
            if (notBlankCount < LENGTH) {
                add(letter)
            }
        }
    }

    fun excludeLetters(word: Word, letters: List<Letter>) {
        val lettersExcluded = lettersExcluded.map { it.value }

        val shuffled = alphabet
            .filterNot { word.value.contains(it) }
            .filterNot { lettersExcluded.contains(it) }
            .shuffled()

        with(shuffled) {
            this@Board.lettersExcluded.addAll(take(3).map { Letter(0, it, Letter.State.Excluded()) })
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
            with(maximumAttempt.getAndIncrement()) {
                incrementAttempt()
                _lines.add(Line(this))
            }
        }
    }

    fun incrementAttempt() {
        _attempt.increment()
    }

    inline fun <reified R: Letter.State> List<Letter>.filterWithState(): List<Letter> {
        return filter { it.state is R }
    }

    inline fun <reified R: Letter.State> filterWithState(): List<Letter> {
        return letters.filterWithState<R>()
    }
}