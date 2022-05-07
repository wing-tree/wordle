package com.wing.tree.android.wordle.domain.usecase.statistics

import com.wing.tree.android.wordle.domain.model.Result
import com.wing.tree.android.wordle.domain.repository.StatisticsRepository
import com.wing.tree.android.wordle.domain.usecase.core.CoroutineUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateStatisticsUseCase @Inject constructor(
    private val repository: StatisticsRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<UpdateStatisticsUseCase.Parameter, Unit>(coroutineDispatcher) {
    override suspend fun execute(parameter: Parameter) {
        repository.update(parameter.result, parameter.guess)
    }

    data class Parameter(val result: Result, val guess: Int)
}