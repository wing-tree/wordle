package com.wing.tree.android.wordle.domain.usecase.item

import com.wing.tree.android.wordle.domain.repository.ItemCountRepository
import com.wing.tree.android.wordle.domain.repository.PreferencesRepository
import com.wing.tree.android.wordle.domain.usecase.core.CoroutineUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ConsumeItemCountUseCase @Inject constructor(
    private val repository: ItemCountRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<Int, Result<Int>>(coroutineDispatcher) {
    override suspend fun execute(parameter: Int): Result<Int> {
        return repository.consume(parameter)
    }
}