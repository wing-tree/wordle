package com.wing.tree.android.wordle.domain.usecase.statistics

import com.wing.tree.android.wordle.domain.model.Statistics
import com.wing.tree.android.wordle.domain.repository.StatisticsRepository
import com.wing.tree.android.wordle.domain.usecase.core.FlowUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import com.wing.tree.android.wordle.domain.usecase.core.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetStatisticsUseCase @Inject constructor(
    private val repository: StatisticsRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, Statistics>(coroutineDispatcher) {
    override fun execute(parameter: Unit): Flow<Result<Statistics>> {
        return repository.get()
            .map {
                try {
                    Result.Success(it)
                } catch (throwable: Throwable) {
                    Result.Error(throwable)
                }
            }
    }
}
