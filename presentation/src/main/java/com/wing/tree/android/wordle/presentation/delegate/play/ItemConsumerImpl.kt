package com.wing.tree.android.wordle.presentation.delegate.play

import com.wing.tree.android.wordle.domain.model.item.Item.Type
import com.wing.tree.android.wordle.domain.model.item.ItemCount
import com.wing.tree.android.wordle.domain.usecase.billing.ConsumeCreditsUseCase
import com.wing.tree.android.wordle.domain.usecase.core.getOrDefault
import com.wing.tree.android.wordle.domain.usecase.core.getOrNull
import com.wing.tree.android.wordle.domain.usecase.item.ConsumeItemCountUseCase
import com.wing.tree.android.wordle.domain.usecase.item.GetItemCountUseCase
import com.wing.tree.wordle.core.exception.NotEnoughCreditException
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

    override suspend fun consume(type: Type): Result<Type> {
        val result = itemCount.first()
        val itemCount = when(type) {
            Type.Eraser -> result.eraser
            Type.Hint -> result.hint
            Type.OneMoreTry -> result.oneMoreTry
            else -> throw IllegalArgumentException("$type")
        }

        return if (itemCount > 0) {
            consumeItemCountUseCase(type).getOrNull() ?: Result.failure(NullPointerException("$type"))
        } else {
            if (consumeCreditsUseCase(type.credits).getOrDefault(false)) {
                Result.success(type)
            } else {
                Result.failure(NotEnoughCreditException("$type"))
            }
        }
    }
}