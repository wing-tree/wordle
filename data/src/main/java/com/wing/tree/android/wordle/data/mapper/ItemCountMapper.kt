package com.wing.tree.android.wordle.data.mapper

import com.wing.tree.android.wordle.data.datastore.itemcount.ItemCount
import com.wing.tree.android.wordle.domain.model.item.ItemCount as DomainItemCount

object ItemCountMapper {
    fun ItemCount.toDomainModel(): DomainItemCount {
        val itemCount = this

        return object : DomainItemCount {
            override val eraser: Int = itemCount.eraser
            override val hint: Int = itemCount.hint
            override val oneMoreTry: Int = itemCount.oneMoreTry
        }
    }
}