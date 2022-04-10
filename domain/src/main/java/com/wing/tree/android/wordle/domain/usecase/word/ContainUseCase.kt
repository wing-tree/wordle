package com.wing.tree.android.wordle.domain.usecase.word

import com.wing.tree.android.wordle.domain.repository.WordRepository
import com.wing.tree.android.wordle.domain.usecase.core.CoroutineUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import com.wing.tree.android.wordle.domain.usecase.core.Result
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ContainUseCase @Inject constructor(
    private val repository: WordRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<ContainUseCase.Parameter, Result<Boolean>>(coroutineDispatcher) {
    data class Parameter(val letters: String)

    override suspend fun execute(parameter: Parameter): Result<Boolean> {
        return try {
            val letters = parameter.letters

            Result.Success(repository.contain(letters))
        } catch (throwable: Throwable) {
            Result.Error(throwable)
        }
    }
}