package com.wing.tree.android.wordle.presentation.delegate.play

import androidx.annotation.MainThread
import com.wing.tree.android.wordle.android.constant.BLANK
import com.wing.tree.android.wordle.domain.usecase.core.Result
import com.wing.tree.android.wordle.domain.usecase.core.map
import com.wing.tree.android.wordle.domain.usecase.word.ContainUseCase
import com.wing.tree.android.wordle.presentation.model.play.Letter
import com.wing.tree.android.wordle.presentation.model.play.Letters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LettersCheckerImpl(private val containUseCase: ContainUseCase) : LettersChecker {
    private val mainDispatcher = Dispatchers.Main

    override suspend fun submit(
        word: String,
        letters: Letters,
        @MainThread onFailure: (Throwable) -> Unit,
        @MainThread onSuccess: (Letters) -> Unit
    ) {
        containUseCase.invoke(ContainUseCase.Parameter(letters.string)).map { result ->
            when(result) {
                is Result.Error -> withContext(mainDispatcher) { onFailure(result.throwable) }
                is Result.Success -> {
                    if (result.data) {
                        // 단어 검사.
                            var word = word

                        word.forEachIndexed { index, letter ->
                            if (letters[index].letter == "$letter") {
                                word = word.replaceFirst("$letter", BLANK)
                                letters[index].state = Letter.State.Correct.InRightPlace()
                            }
                        }

                        letters.filterIsState<Letter.State.Unknown>().forEach {
                            println("ououououou:$word")
                            if (word.contains(it.letter)) {
                                word = word.replaceFirst(it.letter, BLANK)
                                println("wordininin:$word")

                                it.state = Letter.State.Correct.InWrongPlace()
                            }
                        }

                        letters.filterIsState<Letter.State.Unknown>().forEach {
                            it.state = Letter.State.Incorrect()
                        }

                        withContext(mainDispatcher) {
                            onSuccess.invoke(letters)
                        }
                    } else {
                        // 단어가 없다.
                        // no word exception 만들어서 던져라!.
                    }
                }
            }
        }



        // 제출. onSubmiited
    }
}