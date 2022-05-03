package com.wing.tree.android.wordle.domain.usecase.playstate

import com.wing.tree.android.wordle.domain.model.Statistics
import com.wing.tree.android.wordle.domain.model.playstate.PlayState
import com.wing.tree.android.wordle.domain.repository.PlayStateRepository
import com.wing.tree.android.wordle.domain.repository.StatisticsRepository
import com.wing.tree.android.wordle.domain.usecase.core.FlowUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import com.wing.tree.android.wordle.domain.usecase.core.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPlayStateUseCase @Inject constructor(
    private val repository: PlayStateRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, PlayState>(coroutineDispatcher) {
    override fun execute(parameter: Unit): Flow<Result<PlayState>> {
        return repository.get().map {
            try {
                Result.Success(it)
            } catch (throwable: Throwable) {
                Result.Error(throwable)
            }
        }
    }
}