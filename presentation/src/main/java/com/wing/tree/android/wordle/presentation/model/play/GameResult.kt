package com.wing.tree.android.wordle.presentation.model.play

sealed class GameResult {
    object Lose : GameResult()
    object Win : GameResult()
}