package com.wing.tree.android.wordle.domain.usecase.word

import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.domain.repository.WordRepository
import com.wing.tree.android.wordle.domain.usecase.core.CoroutineUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import com.wing.tree.android.wordle.domain.usecase.core.Result
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetWordUseCase @Inject constructor(
    private val repository: WordRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<GetWordUseCase.Parameter, Result<Word>>(coroutineDispatcher) {
    override suspend fun execute(parameter: Parameter): Result<Word> {
        return try {
            Result.Success(repository.get(parameter.index))
        } catch (throwable: Throwable) {
            Result.Error(throwable)
        }
    }

    data class Parameter(val index: Int)
}