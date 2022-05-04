package com.wing.tree.android.wordle.domain.usecase.playstate

import com.wing.tree.android.wordle.domain.repository.PlayStateRepository
import com.wing.tree.android.wordle.domain.usecase.core.CoroutineUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ClearPlayStateUseCase @Inject constructor(
    private val repository: PlayStateRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<Unit, Unit>(coroutineDispatcher) {
    override suspend fun execute(parameter: Unit) {
        repository.clear()
    }
}