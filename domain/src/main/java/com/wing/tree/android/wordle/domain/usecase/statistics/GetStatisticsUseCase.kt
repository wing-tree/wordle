package com.wing.tree.android.wordle.domain.usecase.statistics

import com.wing.tree.android.wordle.domain.model.staticstics.Statistics
import com.wing.tree.android.wordle.domain.repository.StatisticsRepository
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import com.wing.tree.android.wordle.domain.usecase.core.NoParameterFlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStatisticsUseCase @Inject constructor(
    private val repository: StatisticsRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : NoParameterFlowUseCase<Statistics>(coroutineDispatcher) {
    override fun execute(): Flow<Statistics> {
        return repository.get()
    }
}