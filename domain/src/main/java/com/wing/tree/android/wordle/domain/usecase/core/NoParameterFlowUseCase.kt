package com.wing.tree.android.wordle.domain.usecase.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*

abstract class NoParameterFlowUseCase <R: Any>(private val coroutineDispatcher: CoroutineDispatcher) {
    operator fun invoke(): Flow<Result<R>> = execute()
        .map <R, Result<R>> { Result.Success(it) }
        .catch { emit(Result.Error(it)) }
        .flowOn(coroutineDispatcher)

    protected abstract fun execute(): Flow<R>
}