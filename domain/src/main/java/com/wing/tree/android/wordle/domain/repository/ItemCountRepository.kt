package com.wing.tree.android.wordle.domain.repository

import com.wing.tree.android.wordle.domain.model.item.Item
import com.wing.tree.android.wordle.domain.model.item.ItemCount
import kotlinx.coroutines.flow.Flow

interface ItemCountRepository {
    fun get(): Flow<ItemCount>
    suspend fun consume(type: Item): Result<Item>
}