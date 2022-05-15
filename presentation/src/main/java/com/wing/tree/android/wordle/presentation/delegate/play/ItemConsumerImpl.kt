package com.wing.tree.android.wordle.presentation.delegate.play

import com.wing.tree.android.wordle.domain.model.item.Item.Credits
import com.wing.tree.android.wordle.domain.model.item.Item.Type
import com.wing.tree.android.wordle.domain.model.item.ItemCount
import com.wing.tree.android.wordle.domain.model.item.ItemType
import com.wing.tree.android.wordle.domain.usecase.billing.ConsumeCreditsUseCase
import com.wing.tree.android.wordle.domain.usecase.core.getOrDefault
import com.wing.tree.android.wordle.domain.usecase.core.getOrNull
import com.wing.tree.android.wordle.domain.usecase.item.ConsumeItemCountUseCase
import com.wing.tree.android.wordle.domain.usecase.item.GetItemCountUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ItemConsumerImpl(
    private val consumeCreditsUseCase: ConsumeCreditsUseCase,
    private val consumeItemCountUseCase: ConsumeItemCountUseCase,
    getItemCountUseCase: GetItemCountUseCase
) : ItemConsumer {
    override val itemCount: Flow<ItemCount> = getItemCountUseCase().map {
        it.getOrDefault(object : ItemCount {
            override val eraser: Int = ItemCount.Default.ERASER
            override val hint: Int = ItemCount.Default.HINT
            override val oneMoreTry: Int = ItemCount.Default.ONE_MORE_TRY
        })
    }

    override suspend fun consume(@ItemType itemType: Int): Result<Int> {
        val result = itemCount.first()
        val itemCount = when(itemType) {
            Type.ERASER -> result.eraser
            Type.HINT -> result.hint
            Type.ONE_MORE_TRY -> result.oneMoreTry
            else -> throw IllegalArgumentException("$itemType")
        }

        return if (itemCount > 0) {
            consumeItemCountUseCase(itemType).getOrNull() ?: Result.failure(UnknownError("$itemType"))
        } else {
            val credits = when(itemType) {
                Type.ERASER -> Credits.ERASER
                Type.HINT -> Credits.HINT
                Type.ONE_MORE_TRY -> Credits.ONE_MORE_TRY
                else -> throw IllegalArgumentException("$itemType")
            }

            if (consumeCreditsUseCase(credits).getOrDefault(false)) {
                Result.success(itemType)
            } else {
                // todo 돈 부족 exception.
                Result.failure(UnknownError("$itemType"))
            }
        }
    }
}