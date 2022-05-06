package com.wing.tree.android.wordle.domain.usecase.playstate

import com.wing.tree.android.wordle.domain.model.playstate.PlayState
import com.wing.tree.android.wordle.domain.repository.PlayStateRepository
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import com.wing.tree.android.wordle.domain.usecase.core.NoParameterFlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlayStateUseCase @Inject constructor(
    private val repository: PlayStateRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : NoParameterFlowUseCase<PlayState>(coroutineDispatcher) {
    override fun execute(): Flow<PlayState> {
        return repository.get()
    }
}