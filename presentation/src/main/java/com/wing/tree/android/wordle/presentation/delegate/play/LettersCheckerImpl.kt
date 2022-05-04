package com.wing.tree.android.wordle.presentation.delegate.play

import androidx.annotation.MainThread
import com.wing.tree.android.wordle.android.exception.WordNotFoundException
import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.domain.usecase.core.getOrDefault
import com.wing.tree.android.wordle.domain.usecase.word.ContainsUseCase
import com.wing.tree.android.wordle.presentation.model.play.Letter.State.In
import com.wing.tree.android.wordle.presentation.model.play.Letter.State.NotIn
import com.wing.tree.android.wordle.presentation.model.play.Line
import com.wing.tree.wordle.core.constant.BLANK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LettersCheckerImpl(private val containsUseCase: ContainsUseCase) : LettersChecker {
    private val mainDispatcher = Dispatchers.Main

    override suspend fun submit(
        word: Word,
        line: Line,
        @MainThread onFailure: (Throwable) -> Unit,
        @MainThread onSuccess: (Line) -> Unit
    ) {
        if (containsUseCase(line.string).getOrDefault(false)) {
            var wordValue = word.value

            wordValue.forEachIndexed { index, letter ->
                if (line[index].value == "$letter") {
                    wordValue = wordValue.replaceFirst("$letter", BLANK)
                    line[index].updateState(In.Matched())
                }
            }

            line.unknownLetters.forEach { letter ->
                if (letter.value in wordValue) {
                    wordValue = wordValue.replaceFirst(letter.value, BLANK)

                    letter.updateState(In.Mismatched())
                }
            }

            line.unknownLetters.forEach {
                it.updateState(NotIn())
            }

            withContext(mainDispatcher) {
                onSuccess(line)
            }
        } else {
            withContext(mainDispatcher) {
                onFailure(WordNotFoundException(""))
            }
        }
    }
}