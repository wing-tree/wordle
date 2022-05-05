package com.wing.tree.android.wordle.presentation.delegate.play

import androidx.annotation.MainThread
import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.domain.usecase.core.getOrDefault
import com.wing.tree.android.wordle.domain.usecase.word.ContainsUseCase
import com.wing.tree.android.wordle.presentation.model.play.Letter.State.In
import com.wing.tree.android.wordle.presentation.model.play.Letter.State.NotIn
import com.wing.tree.android.wordle.presentation.model.play.Line
import com.wing.tree.wordle.core.constant.BLANK
import com.wing.tree.wordle.core.constant.EMPTY
import com.wing.tree.wordle.core.exception.WordNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LettersCheckerImpl(private val containsUseCase: ContainsUseCase) : LettersChecker {
    private val mainDispatcher = Dispatchers.Main
    private var word: String = EMPTY

    override suspend fun submit(
        word: Word,
        line: Line,
        @MainThread onFailure: (Throwable) -> Unit,
        @MainThread onSuccess: (Line) -> Unit
    ) {
        if (containsUseCase(line.string).getOrDefault(false)) {
            this.word = word.value

            processMatchingLetters(line)
            processMismatchingLetters(line)
            processNotInLetters(line)

            withContext(mainDispatcher) {
                onSuccess(line)
            }
        } else {
            withContext(mainDispatcher) {
                onFailure(WordNotFoundException())
            }
        }
    }

    private fun processMatchingLetters(line: Line) {
        this.word.forEachIndexed { index, letter ->
            if (line[index].value == "$letter") {
                this.word = this.word.replaceFirst("$letter", BLANK)
                line[index].updateState(In.Matched())
            }
        }
    }

    private fun processMismatchingLetters(line: Line) {
        line.undefinedLetters.forEach { letter ->
            if (letter.value in word) {
                word = word.replaceFirst(letter.value, BLANK)

                letter.updateState(In.Mismatched())
            }
        }
    }

    private fun processNotInLetters(line: Line) {
        line.undefinedLetters.forEach {
            it.updateState(NotIn())
        }
    }
}