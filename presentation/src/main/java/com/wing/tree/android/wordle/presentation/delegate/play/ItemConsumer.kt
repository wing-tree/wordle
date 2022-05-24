package com.wing.tree.android.wordle.presentation.delegate.play

import com.wing.tree.android.wordle.domain.model.item.Item
import com.wing.tree.android.wordle.domain.model.item.ItemCount
import kotlinx.coroutines.flow.Flow

interface ItemConsumer {
    val itemCount: Flow<ItemCount>

    suspend fun consume(type: Item.Type): Result<Item.Type>
}