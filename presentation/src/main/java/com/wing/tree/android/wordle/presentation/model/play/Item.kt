package com.wing.tree.android.wordle.presentation.model.play

sealed class Item {
    abstract val count: Int
    abstract val price: Int

    data class Eraser(
        override val count: Int,
        override val price: Int
    ) : Item()

    data class Hint(
        override val count: Int,
        override val price: Int
    ) : Item()

    data class OneMoreTry(
        override val count: Int,
        override val price: Int
    ) : Item()
}
