package com.wing.tree.android.wordle.domain.model

sealed class Result {
    object Lose : Result()
    object Win : Result()
}