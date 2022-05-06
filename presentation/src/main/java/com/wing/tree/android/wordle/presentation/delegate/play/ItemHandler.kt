package com.wing.tree.android.wordle.presentation.delegate.play

import com.wing.tree.android.wordle.domain.model.item.ItemCount
import com.wing.tree.android.wordle.domain.model.item.ItemType
import kotlinx.coroutines.flow.Flow

interface ItemHandler {
    val itemCount: Flow<ItemCount>

    suspend fun use(@ItemType itemType: Int): Result<Int>
}