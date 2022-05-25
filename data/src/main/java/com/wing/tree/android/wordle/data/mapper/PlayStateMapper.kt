package com.wing.tree.android.wordle.data.mapper

import com.wing.tree.android.wordle.data.datastore.playstate.*
import com.wing.tree.android.wordle.domain.model.playstate.Key
import com.wing.tree.android.wordle.domain.model.Word as DomainWord
import com.wing.tree.android.wordle.domain.model.playstate.Keyboard as DomainKeyboard
import com.wing.tree.android.wordle.domain.model.playstate.Letter as DomainLetter
import com.wing.tree.android.wordle.domain.model.playstate.Line as DomainLine
import com.wing.tree.android.wordle.domain.model.playstate.PlayBoard as DomainPlayBoard
import com.wing.tree.android.wordle.domain.model.playstate.PlayState as DomainPlayState

fun PlayState.toDomainModel(): DomainPlayState {
    val playState = this

    return object : DomainPlayState {
        override val keyboard: DomainKeyboard = playState.keyboard.toDomainModel()
        override val playBoard: DomainPlayBoard = playState.playBoard.toDomainModel()
        override val word: DomainWord = playState.word.toDomainModel()
    }
}

fun DomainKeyboard.toDataModel(): Keyboard {
    return Keyboard.newBuilder()
        .addAllAlphabet(alphabets.map { it.toDataModel() })
        .build()
}

fun Keyboard.toDomainModel(): DomainKeyboard {
    return object : DomainKeyboard {
        override val alphabets: List<Key.Alphabet> = alphabetList.map { it.toDomainModel() }
    }
}

fun Key.Alphabet.toDataModel(): AlphabetKey {
    return AlphabetKey.newBuilder()
        .setLetter(letter)
        .setState(state)
        .build()
}

fun AlphabetKey.toDomainModel(): Key.Alphabet {
    val alphabet = this

    return object : Key.Alphabet {
        override val letter: String = alphabet.letter
        override val state: Int = alphabet.state
    }
}

fun DomainPlayBoard.toDataModel(): PlayBoard {
    return PlayBoard.newBuilder()
        .setRound(round)
        .setMaximumRound(lastRound)
        .addAllLine(lines.map { it.toDataModel() })
        .build()
}


fun PlayBoard.toDomainModel(): DomainPlayBoard {
    val playBoard = this
    val lineList = playBoard.lineList
    val lines = lineList.map { it.toDomainModel() }

    return object : DomainPlayBoard {
        override val round: Int = playBoard.round
        override val lastRound: Int = playBoard.maximumRound
        override val lines: List<DomainLine> = lines
    }
}

private fun DomainLine.toDataModel(): Line {
    return Line.newBuilder()
        .setRound(round)
        .addAllCurrentLetter(currentLetters.map { it.toDataModel() })
        .addAllPreviousLetter(previousLetters.map { it.toDataModel() })
        .setIsFocused(isFocused)
        .setIsSubmitted(isSubmitted)
        .build()
}

private fun Line.toDomainModel(): DomainLine {
    val line = this
    val currentLetters = line.currentLetterList
    val previousLetters = line.previousLetterList

    return object : DomainLine {
        override val round: Int = line.round
        override val currentLetters: List<DomainLetter>
            get() = currentLetters.map { it.toDomainModel() }
        override val previousLetters: List<DomainLetter>
            get() = previousLetters.map { it.toDomainModel() }
        override val isFocused: Boolean = line.isFocused
        override val isSubmitted: Boolean = line.isSubmitted
    }
}

private fun DomainLetter.toDataModel(): Letter {
    return Letter.newBuilder()
        .setPosition(position)
        .setValue(value)
        .setState(state)
        .setIsSubmitted(isSubmitted)
        .build()
}

private fun Letter.toDomainModel(): DomainLetter {
    val letter = this

    return object : DomainLetter {
        override val position: Int = letter.position
        override val value: String = letter.value
        override val state: Int = letter.state
        override val isSubmitted: Boolean = letter.isSubmitted
    }
}

fun DomainWord.toDataModel(): Word {
    return Word.newBuilder()
        .setIndex(index)
        .setValue(value)
        .build()
}

fun Word.toDomainModel(): DomainWord {
    val dataWord = this

    return object : DomainWord {
        override val index: Int = dataWord.index
        override val value: String = dataWord.value
    }
}