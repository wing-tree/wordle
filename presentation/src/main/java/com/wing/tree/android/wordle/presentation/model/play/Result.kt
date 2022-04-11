package com.wing.tree.android.wordle.presentation.model.play

sealed class Result {
    object Lose : Result()
    object Win : Result()
}