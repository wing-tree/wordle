package com.wing.tree.android.wordle.domain.usecase.item

import com.wing.tree.android.wordle.domain.model.item.Item
import com.wing.tree.android.wordle.domain.repository.ItemCountRepository
import com.wing.tree.android.wordle.domain.usecase.core.CoroutineUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ConsumeItemCountUseCase @Inject constructor(
    private val repository: ItemCountRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<Item, Result<Item>>(coroutineDispatcher) {
    override suspend fun execute(parameter: Item): Result<Item> {
        return repository.consume(parameter)
    }
}