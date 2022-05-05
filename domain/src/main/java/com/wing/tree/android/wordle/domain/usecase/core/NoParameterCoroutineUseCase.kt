package com.wing.tree.android.wordle.domain.usecase.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class NoParameterCoroutineUseCase<R: Any>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(): Result<R> {
        return try {
            withContext(coroutineDispatcher) {
                Result.Success(execute())
            }
        } catch (t: Throwable) {
            Result.Error(t)
        }
    }

    protected abstract suspend fun execute(): R
}