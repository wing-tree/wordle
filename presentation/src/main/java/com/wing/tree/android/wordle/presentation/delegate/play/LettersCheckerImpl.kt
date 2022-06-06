package com.wing.tree.android.wordle.presentation.delegate.play

import com.wing.tree.android.wordle.domain.usecase.core.getOrDefault
import com.wing.tree.android.wordle.domain.usecase.word.ContainsUseCase
import com.wing.tree.android.wordle.presentation.model.play.Letter.State.In
import com.wing.tree.android.wordle.presentation.model.play.Letter.State.NotIn
import com.wing.tree.android.wordle.presentation.model.play.Line
import com.wing.tree.wordle.core.constant.BLANK
import com.wing.tree.wordle.core.constant.EMPTY
import com.wing.tree.wordle.core.exception.WordNotFoundException

class LettersCheckerImpl(private val containsUseCase: ContainsUseCase) : LettersChecker {
    private var answer: String = EMPTY

    override suspend fun submit(answer: String, line: Line): Result<Line> {
        return if (containsUseCase(line.letters).getOrDefault(false)) {
            this.answer = answer

            checkMatchingLetters(line)
            checkMismatchingLetters(line)
            checkNotInLetters(line)

            Result.success(line)
        } else {
            Result.failure(WordNotFoundException())
        }
    }

    private fun checkMatchingLetters(line: Line) {
        val zip = answer zip line.letters

        zip.forEachIndexed { index, (a, b) ->
            if (a == b) {
                answer = answer.replaceFirst("$a", BLANK)

                line[index].updateState(In.Matched())
            }
        }
    }

    private fun checkMismatchingLetters(line: Line) {
        line.undefinedLetters.forEach { letter ->
            if (letter.value in answer) {
                answer = answer.replaceFirst(letter.value, BLANK)

                letter.updateState(In.Mismatched())
            }
        }
    }

    private fun checkNotInLetters(line: Line) {
        line.undefinedLetters.forEach {
            it.updateState(NotIn())
        }
    }
}