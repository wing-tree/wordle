package com.wing.tree.android.wordle.domain.usecase.playstate

import com.wing.tree.android.wordle.domain.model.playstate.PlayState
import com.wing.tree.android.wordle.domain.repository.PlayStateRepository
import com.wing.tree.android.wordle.domain.usecase.core.CoroutineUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdatePlayStateUseCase @Inject constructor(
    private val repository: PlayStateRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<PlayState, Unit>(coroutineDispatcher) {
    override suspend fun execute(parameter: PlayState) {
        repository.update(parameter)
    }
}