package com.wing.tree.android.wordle.presentation.delegate.play

import androidx.annotation.MainThread
import com.wing.tree.android.wordle.android.constant.BLANK
import com.wing.tree.android.wordle.android.exception.WordNotFoundException
import com.wing.tree.android.wordle.domain.usecase.core.Result
import com.wing.tree.android.wordle.domain.usecase.core.map
import com.wing.tree.android.wordle.domain.usecase.word.ContainUseCase
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.model.play.Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LettersCheckerImpl(private val containUseCase: ContainUseCase) : LettersChecker {
    private val mainDispatcher = Dispatchers.Main

    override suspend fun submit(
        word: String,
        line: Line,
        @MainThread onFailure: (Throwable) -> Unit,
        @MainThread onSuccess: (Line) -> Unit
    ) {
        containUseCase.invoke(ContainUseCase.Parameter(line.string)).map { result ->
            when(result) {
                is Result.Error -> withContext(mainDispatcher) { onFailure(result.throwable) }
                is Result.Success -> {
                    if (result.data) {
                        // 단어 검사.
                            var word = word

                        word.forEachIndexed { index, letter ->
                            if (line[index].value == "$letter") {
                                word = word.replaceFirst("$letter", BLANK)
                                line[index].state = Letter.State.Included.Matched()
                            }
                        }

                        line.filterWithState<Letter.State.Unknown>().forEach {
                            if (word.contains(it.value)) {
                                word = word.replaceFirst(it.value, BLANK)

                                it.state = Letter.State.Included.NotMatched()
                            }
                        }

                        line.filterWithState<Letter.State.Unknown>().forEach {
                            it.state = Letter.State.Excluded()
                        }

                        withContext(mainDispatcher) {
                            onSuccess(line)
                        }
                    } else {
                        // 단어가 없다.
                        // no word exception 만들어서 던져라!.
                        onFailure(WordNotFoundException(word))
                    }
                }
                Result.Loading -> Unit
            }
        }
    }
}