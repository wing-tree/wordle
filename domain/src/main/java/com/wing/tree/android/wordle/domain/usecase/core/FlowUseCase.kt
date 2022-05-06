package com.wing.tree.android.wordle.domain.usecase.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

abstract class FlowUseCase<in P: Any, R: Any>(private val coroutineDispatcher: CoroutineDispatcher) {
    operator fun invoke(parameter: P): Flow<Result<R>> = execute(parameter)
        .map <R, Result<R>>{ Result.Success(it) }
        .catch { cause: Throwable -> emit(Result.Error(cause)) }
        .flowOn(coroutineDispatcher)

    protected abstract fun execute(parameter: P): Flow<R>
}