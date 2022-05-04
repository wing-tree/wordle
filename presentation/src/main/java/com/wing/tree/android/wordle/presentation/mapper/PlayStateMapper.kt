package com.wing.tree.android.wordle.presentation.mapper

import com.wing.tree.android.wordle.presentation.model.play.*
import com.wing.tree.android.wordle.domain.model.playstate.Key.Alphabet as DomainAlphabet
import com.wing.tree.android.wordle.domain.model.playstate.Keyboard as DomainKeyboard
import com.wing.tree.android.wordle.domain.model.playstate.Letter as DomainLetter
import com.wing.tree.android.wordle.domain.model.playstate.Line as DomainLine
import com.wing.tree.android.wordle.domain.model.playstate.PlayBoard as DomainPlayBoard

object PlayStateMapper {
    fun Keyboard.toDomainModel(): DomainKeyboard {
        val keyboard = this

        return object : DomainKeyboard {
            override val alphabets: List<DomainAlphabet> = keyboard.alphabets.map { it.toDomainModel() }
        }
    }

    fun Key.Alphabet.toDomainModel(): DomainAlphabet {
        val alphabet = this

        return object : DomainAlphabet {
            override val letter: String = alphabet.letter
            override val state: Int = alphabet.state.toInt()
        }
    }

    fun PlayBoard.toDomainModel(): DomainPlayBoard {
        val playBoard = this

        return object : DomainPlayBoard {
            override val round: Int = playBoard.round
            override val maximumRound: Int = playBoard.maximumRound
            override val isRoundAdded: Boolean = playBoard.isRoundAdded
            override val lines: List<DomainLine> = playBoard.lines.map { it.toDomainModel() }
        }
    }

    fun DomainLine.toPresentationModel() = Line(round = round)

    fun Line.toDomainModel(): DomainLine {
        val line = this

        return object : DomainLine {
            override val round: Int = line.round
            override val letters: List<DomainLetter> = line.letters.map { it.toDomainModel() }
            override val previousLetters: List<DomainLetter> = line.previousLetters.map { it.toDomainModel() }
            override val isSubmitted: Boolean = line.isSubmitted
        }
    }

    fun DomainLetter.toPresentationModel() = Letter(position, value)

    fun Letter.toDomainModel(): DomainLetter {
        val letter = this

        return object : DomainLetter {
            override val position: Int = letter.position
            override val value: String = letter.value
            override val state: Int = letter.state.toInt()
            override val isSubmitted: Boolean = letter.isSubmitted
        }
    }
}