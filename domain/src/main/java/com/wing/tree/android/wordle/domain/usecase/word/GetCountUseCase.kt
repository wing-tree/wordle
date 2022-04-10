package com.wing.tree.android.wordle.domain.usecase.word

import com.wing.tree.android.wordle.domain.repository.WordRepository
import com.wing.tree.android.wordle.domain.usecase.core.CoroutineUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import com.wing.tree.android.wordle.domain.usecase.core.Result
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetCountUseCase @Inject constructor(
    private val repository: WordRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<Unit, Result<Int>>(coroutineDispatcher) {
    override suspend fun execute(parameter: Unit): Result<Int> {
        return try {
            Result.Success(repository.count())
        } catch (throwable: Throwable) {
            Result.Error(throwable)
        }
    }
}