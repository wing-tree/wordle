package com.wing.tree.android.wordle.presentation.model.play

sealed class State {
    data class Error(val throwable: Throwable): State()
    object Play: State()
    object Ready: State()

    sealed class Finish : State() {
        object Lose : Finish()
        object Win : Finish()
    }
}