package com.wing.tree.android.wordle.presentation.model.play

import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.presentation.mapper.PlayStateMapper.toPresentationModel
import com.wing.tree.android.wordle.presentation.model.play.Letter.State
import com.wing.tree.android.wordle.presentation.model.play.Letter.State.In
import com.wing.tree.wordle.core.constant.MAXIMUM_ROUND
import com.wing.tree.wordle.core.constant.WORD_LENGTH
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import com.wing.tree.android.wordle.domain.model.playstate.PlayBoard as DomainPlayBoard

class PlayBoard {
    private var _round: Int = 0
    val round: Int get() = _round

    private var _lastRound: Int = MAXIMUM_ROUND
    val lastRound: Int get() = _lastRound

    private val _lines = MutableList(MAXIMUM_ROUND) { Line(it) }
    val lines: List<Line> get() = _lines

    private val letters get() = lines.flatten()

    val currentLine: Line get() = lines[round]

    val isRoundAdded: Boolean get() = lastRound > MAXIMUM_ROUND
    val isRoundOver: Boolean get() = round.inc() >= lastRound
    val runsAnimation = AtomicBoolean(false)

    val closest: Line get() = lines.maxByOrNull { it.proximity } ?: currentLine

    val notUnknownLetters: List<Letter> get() = letters.filter { it.state.notUndefined }

    init {
        lines.first().requestFocus()
    }

    fun add(letter: String) {
        with(currentLine) {
            if (notBlankCount < WORD_LENGTH) {
                add(letter)
            }
        }
    }

    fun availableHints(word: Word): List<Letter> {
        val distinct = matched().map { it.position }.distinct()

        return mutableListOf<Letter>().apply {
            word.forEachIndexed { index, letter ->
                if (index !in distinct) {
                    add(Letter(index, letter))
                }
            }
        }
    }

    fun availableHintCount(word: Word) = availableHints(word).count()

    fun matched() = filterIsState<In.Matched>()

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
        currentLine.removeFocus()
        currentLine.submit()
    }

    fun addRound() {
        incrementRound()

        val line = Line(_lastRound++).apply {
            requestFocus()
        }

        _lines.add(line)
    }

    fun incrementRound() {
        ++_round
    }

    private inline fun <reified R: State> List<Letter>.filterIsState(): List<Letter> {
        return filter { it.state is R }
    }

    private inline fun <reified R: State> filterIsState(): List<Letter> {
        return letters.filterIsState<R>()
    }

    companion object {
        fun from(playBoard: DomainPlayBoard): PlayBoard {
            return PlayBoard().apply {
                _round = playBoard.round
                _lastRound = playBoard.maximumRound

                if (lines.size < lastRound) {
                    _lines.add(Line(lastRound.dec()))
                }

                playBoard.lines.forEachIndexed { index, line ->
                    _lines[index] = line.toPresentationModel()
                }
            }
        }
    }
}