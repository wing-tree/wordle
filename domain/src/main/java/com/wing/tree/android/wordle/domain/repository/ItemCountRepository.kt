package com.wing.tree.android.wordle.domain.repository

import com.wing.tree.android.wordle.domain.model.item.ItemCount
import com.wing.tree.android.wordle.domain.model.item.ItemType
import kotlinx.coroutines.flow.Flow

interface ItemCountRepository {
    fun get(): Flow<ItemCount>
    suspend fun consume(@ItemType itemType: Int): Result<Int>
}