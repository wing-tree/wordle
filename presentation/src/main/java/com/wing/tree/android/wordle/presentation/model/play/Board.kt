package com.wing.tree.android.wordle.presentation.model.play

import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.presentation.constant.Round
import com.wing.tree.android.wordle.presentation.constant.Word.LENGTH
import com.wing.tree.android.wordle.presentation.util.increment
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class Board {
    private val _round = AtomicInteger(0)
    val round: Int get() = _round.get()

    private val maximumRound = AtomicInteger(Round.MAXIMUM)

    val isRoundAdded = AtomicBoolean(false)
    val isRoundExceeded: Boolean get() = round >= maximumRound.get().dec()

    private val _lines = MutableList(Round.MAXIMUM) { Line(it) }
    val lines: List<Line> get() = _lines

    val letters get() = lines.flatten()

    val currentLine: Line get() = lines[_round.get()]

    val notUnknownLetters: List<Letter> get() = letters.filter { it.state.notUnknown }

    fun getNotMatchedYetLetters(word: Word): List<Letter> {
        val matchedPositions = letters.filterWithState<Letter.State.In.Matched>().map { it.position }.distinct()

        return mutableListOf<Letter>().apply {
            word.value.forEachIndexed { index, letter ->
                if (matchedPositions.contains(index).not()) {
                    add(Letter(index, letter))
                }
            }
        }
    }

    fun add(letter: String) {
        with(currentLine) {
            if (notBlankCount < LENGTH) {
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

    fun addRound() {
        if (isRoundAdded.compareAndSet(false, true)) {
            with(maximumRound.getAndIncrement()) {
                incrementRound()
                _lines.add(Line(this))
            }
        }
    }

    fun incrementRound() {
        _round.increment()
    }

    inline fun <reified R: Letter.State> List<Letter>.filterWithState(): List<Letter> {
        return filter { it.state is R }
    }

    inline fun <reified R: Letter.State> filterWithState(): List<Letter> {
        return letters.filterWithState<R>()
    }
}