package com.wing.tree.android.wordle.domain.usecase.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

abstract class NoParameterFlowUseCase <R: Any>(private val coroutineDispatcher: CoroutineDispatcher) {
    operator fun invoke(): Flow<Result<R>> = execute()
        .catch { cause: Throwable -> emit(Result.Error(cause)) }
        .flowOn(coroutineDispatcher)

    protected abstract fun execute(): Flow<Result<R>>
}