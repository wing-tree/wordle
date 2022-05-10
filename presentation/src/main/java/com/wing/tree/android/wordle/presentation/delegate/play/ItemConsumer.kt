package com.wing.tree.android.wordle.presentation.delegate.play

import com.wing.tree.android.wordle.domain.model.item.ItemCount
import com.wing.tree.android.wordle.domain.model.item.ItemType
import kotlinx.coroutines.flow.Flow

interface ItemConsumer {
    val itemCount: Flow<ItemCount>

    suspend fun consume(@ItemType itemType: Int): Result<Int>
}