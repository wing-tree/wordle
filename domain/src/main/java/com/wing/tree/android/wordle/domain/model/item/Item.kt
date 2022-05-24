package com.wing.tree.android.wordle.domain.model.item

object Item {
    private object Credits {
        const val ERASER = 40
        const val HINT = 80
        const val ONE_MORE_TRY = 120
    }

    enum class Type(val credits: Int) {
        Eraser(Credits.ERASER),
        Hint(Credits.HINT),
        OneMoreTry(Credits.ONE_MORE_TRY)
    }
}