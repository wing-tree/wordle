package com.wing.tree.android.wordle.domain.usecase.item

import com.wing.tree.android.wordle.domain.model.item.Item
import com.wing.tree.android.wordle.domain.model.item.ItemCount
import com.wing.tree.android.wordle.domain.repository.ItemCountRepository
import com.wing.tree.android.wordle.domain.repository.PreferencesRepository
import com.wing.tree.android.wordle.domain.usecase.core.FlowUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import com.wing.tree.android.wordle.domain.usecase.core.NoParameterFlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetItemCountUseCase @Inject constructor(
    private val repository: ItemCountRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : NoParameterFlowUseCase<ItemCount>(coroutineDispatcher) {
    override fun execute(): Flow<ItemCount> {
        return repository.get()
    }
}