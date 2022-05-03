package com.wing.tree.android.wordle.domain.model

sealed class Result {
    abstract val guess: Int

    data class Lose(override val guess: Int) : Result()
    data class Win(override val guess: Int) : Result()
}