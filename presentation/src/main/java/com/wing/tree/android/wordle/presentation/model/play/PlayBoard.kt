package com.wing.tree.android.wordle.presentation.model.play

import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.presentation.mapper.PlayStateMapper.toPresentationModel
import com.wing.tree.wordle.core.constant.MAXIMUM_ROUND
import com.wing.tree.wordle.core.constant.WORD_LENGTH
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import com.wing.tree.android.wordle.domain.model.playstate.PlayBoard as DomainPlayBoard

class PlayBoard {
    private var _round: Int = 0
    val round: Int get() = _round

    private var _maximumRound: Int = MAXIMUM_ROUND
    val maximumRound: Int get() = _maximumRound

    private val _lines = MutableList(MAXIMUM_ROUND) { Line(it) }
    val lines: List<Line> get() = _lines

    val isRoundAdded: Boolean get() = maximumRound > MAXIMUM_ROUND
    val isRoundOver: Boolean get() = round.inc() >= maximumRound
    val runsAnimation = AtomicBoolean(false)

    val letters get() = lines.flatten()
    val closest: Line get() = lines.maxByOrNull { it.proximity } ?: currentLine
    val currentLine: Line get() = lines[round]
    val notUnknownLetters: List<Letter> get() = letters.filter { it.state.notUndefined }

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
            if (notBlankCount < WORD_LENGTH) {
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
        incrementRound()
        _lines.add(Line(_maximumRound++))
    }

    fun incrementRound() {
        ++_round
    }

    inline fun <reified R: Letter.State> List<Letter>.filterWithState(): List<Letter> {
        return filter { it.state is R }
    }

    inline fun <reified R: Letter.State> filterWithState(): List<Letter> {
        return letters.filterWithState<R>()
    }

    companion object {
        fun from(playBoard: DomainPlayBoard): PlayBoard {
            return PlayBoard().apply {
                _round = playBoard.round
                _maximumRound = playBoard.maximumRound

                playBoard.lines.forEachIndexed { index, line ->
                    _lines[index] = line.toPresentationModel()
                }
            }
        }
    }
}