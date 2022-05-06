package com.wing.tree.android.wordle.domain.model.item

import androidx.annotation.IntDef
import com.wing.tree.android.wordle.domain.model.item.Item.Type.ERASER
import com.wing.tree.android.wordle.domain.model.item.Item.Type.HINT
import com.wing.tree.android.wordle.domain.model.item.Item.Type.ONE_MORE_TRY

object Item {
    object Credits {
        const val ERASER = 120
        const val HINT = 120
        const val ONE_MORE_TRY = 120
    }

    object Type {
        const val ERASER = 0
        const val HINT = 1
        const val ONE_MORE_TRY = 2
    }
}

@IntDef(ERASER, HINT, ONE_MORE_TRY)
@Retention(AnnotationRetention.SOURCE)
annotation class ItemType