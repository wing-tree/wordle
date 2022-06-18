package com.wing.tree.android.wordle.domain.model.item

enum class Item(val credits: Int) {
    Eraser(Credits.ERASER),
    Hint(Credits.HINT),
    OneMoreTry(Credits.ONE_MORE_TRY);

    object Credits {
        const val ERASER = 40
        const val HINT = 80
        const val ONE_MORE_TRY = 120
    }
}