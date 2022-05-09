package com.wing.tree.android.wordle.presentation.model.play

sealed class ViewState {
    data class Error(val throwable: Throwable): ViewState()
    data class RoundOver(val isRoundAdded: Boolean) : ViewState()
    object Loading: ViewState()
    object Play: ViewState()
    object Ready: ViewState()

    sealed class Finish : ViewState() {
        data class Lose(val playResult: PlayResult.Lose) : Finish()
        data class Win(val playResult: PlayResult.Win) : Finish()
    }
}