package com.wing.tree.android.wordle.presentation.model.play

sealed class ViewState {
    data class Error(val throwable: Throwable): ViewState()
    data class RoundOver(val isRoundAdded: Boolean) : ViewState()
    object Loading: ViewState()
    object Play: ViewState()
    object Ready: ViewState()

    sealed class Finish : ViewState() {
        abstract val playResult: PlayResult

        data class Lose(override val playResult: PlayResult.Lose) : Finish()
        data class Win(override val playResult: PlayResult.Win) : Finish()
    }
}