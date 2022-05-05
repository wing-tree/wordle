package com.wing.tree.android.wordle.presentation.model.play

sealed class ViewState {
    data class Error(val throwable: Throwable): ViewState()
    object Loading: ViewState()
    object Play: ViewState()
    object Ready: ViewState()

    sealed class Finish : ViewState() {
        class RoundOver(val isRoundAdded: Boolean) : Finish()
        object Win : Finish()
    }
}