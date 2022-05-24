package com.wing.tree.android.wordle.data.repository

import androidx.datastore.core.DataStore
import com.wing.tree.android.wordle.data.datastore.itemcount.ItemCount
import com.wing.tree.android.wordle.data.mapper.ItemCountMapper.toDomainModel
import com.wing.tree.android.wordle.domain.model.item.Item
import com.wing.tree.android.wordle.domain.model.item.ItemCount as DomainItemCount
import com.wing.tree.android.wordle.domain.repository.ItemCountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ItemCountRepositoryImpl @Inject constructor(private val dataStore: DataStore<ItemCount>) : ItemCountRepository {
    override fun get(): Flow<DomainItemCount> {
        return dataStore.data.map { it.toDomainModel() }
    }

    override suspend fun consume(type: Item.Type): Result<Item.Type> {
        return try {
            dataStore.updateData {
                val builder = it.toBuilder()

                when (type) {
                    Item.Type.Eraser -> builder.eraser = it.eraser.dec()
                    Item.Type.Hint -> builder.hint = it.hint.dec()
                    Item.Type.OneMoreTry -> builder.oneMoreTry = it.oneMoreTry.dec()
                }

                builder.build()
            }

            Result.success(type)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}