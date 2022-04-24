package com.wing.tree.android.wordle.presentation.delegate.play

import androidx.annotation.MainThread
import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.domain.usecase.core.Result
import com.wing.tree.android.wordle.domain.usecase.core.map
import com.wing.tree.android.wordle.domain.usecase.word.GetCountUseCase
import com.wing.tree.android.wordle.domain.usecase.word.GetWordUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class WordLoaderImpl(
    private val getCountUseCase: GetCountUseCase,
    private val getWordUseCase: GetWordUseCase
) : WordLoader {
    private val mainDispatcher = Dispatchers.Main

    override suspend fun load(onSuccess: (Word) -> Unit, onFailure: (Throwable) -> Unit) {
        getCountUseCase.invoke(Unit).map { result ->
            when (result) {
                is Result.Error -> onFailure(result.throwable)
                is Result.Success -> {
                    withContext(mainDispatcher) {
                        val index = Random.nextInt(result.data).inc()

                        load(index, onSuccess, onFailure)
                    }
                }
                Result.Loading -> Unit
            }
        }
    }

    private suspend fun load(
        index: Int,
        @MainThread onSuccess: (Word) -> Unit,
        @MainThread onFailure: (Throwable) -> Unit
    ) {
        getWordUseCase.invoke(GetWordUseCase.Parameter(index)).map { result ->
            when(result) {
                is Result.Error -> onFailure(result.throwable)
                is Result.Success -> {
                    withContext(mainDispatcher) {
                        onSuccess(result.data)
                    }
                }
                Result.Loading -> Unit
            }
        }
    }
}