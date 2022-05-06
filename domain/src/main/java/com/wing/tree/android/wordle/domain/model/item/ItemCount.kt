package com.wing.tree.android.wordle.domain.model.item

interface ItemCount {
    val eraser: Int
    val hint: Int
    val oneMoreTry: Int

    object Default {
        const val ERASER = 3
        const val HINT = 3
        const val ONE_MORE_TRY = 1
    }
}

