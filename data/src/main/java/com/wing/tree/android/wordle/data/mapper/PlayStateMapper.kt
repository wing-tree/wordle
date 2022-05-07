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
    val keyboard = this
    val builder = Keyboard.newBuilder()
        .addAllAlphabet(keyboard.alphabets.map { it.toDataModel() })

    return builder.build()
}

fun Keyboard.toDomainModel(): DomainKeyboard {
    val keyboard = this
    val alphabets = keyboard.alphabetList

    return object : DomainKeyboard {
        override val alphabets: List<Key.Alphabet> = alphabets.map { it.toDomainModel() }
    }
}

fun Key.Alphabet.toDataModel(): AlphabetKey {
    val alphabet = this
    val builder = AlphabetKey.newBuilder().apply {
        letter = alphabet.letter
        state = alphabet.state
    }

    return builder.build()
}

fun AlphabetKey.toDomainModel(): Key.Alphabet {
    val alphabet = this

    return object : Key.Alphabet {
        override val letter: String = alphabet.letter
        override val state: Int = alphabet.state
    }
}

fun DomainPlayBoard.toDataModel(): PlayBoard {
    val playBoard = this

    val builder = PlayBoard.newBuilder().apply {
        round = playBoard.round
        maximumRound = playBoard.maximumRound
        addAllLine(playBoard.lines.map { it.toDataModel() })
    }

    return builder.build()
}


fun PlayBoard.toDomainModel(): DomainPlayBoard {
    val playBoard = this
    val lineList = playBoard.lineList
    val lines = lineList.map { it.toDomainModel() }

    return object : DomainPlayBoard {
        override val round: Int = playBoard.round
        override val maximumRound: Int = playBoard.maximumRound
        override val lines: List<DomainLine> = lines
    }
}

private fun DomainLine.toDataModel(): Line {
    val line = this
    val builder = Line.newBuilder().apply {
        round = line.round
        addAllLetter(line.letters.map { it.toDataModel() })
        addAllPreviousLetter(line.previousLetters.map { it.toDataModel() })
        isSubmitted = line.isSubmitted
    }

    return builder.build()
}

private fun Line.toDomainModel(): DomainLine {
    val line = this
    val letters = line.letterList
    val previousLetters = line.previousLetterList

    return object : DomainLine {
        override val round: Int = line.round
        override val letters: List<DomainLetter>
            get() = letters.map { it.toDomainModel() }
        override val previousLetters: List<DomainLetter>
            get() = previousLetters.map { it.toDomainModel() }
        override val isSubmitted: Boolean = line.isSubmitted
    }
}

private fun DomainLetter.toDataModel(): Letter {
    val letter = this
    val builder = Letter.newBuilder().apply {
        position = letter.position
        value = letter.value
        state = letter.state
        isSubmitted = letter.isSubmitted
    }

    return builder.build()
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
    val word = this
    val builder = Word.newBuilder().apply {
        index = word.index
        value = word.value
    }

    return builder.build()
}

fun Word.toDomainModel(): DomainWord {
    val word = this

    return object : DomainWord {
        override val index: Int = word.index
        override val value: String = word.value
    }
}