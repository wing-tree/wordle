package com.wing.tree.android.wordle.presentation.eventbus

sealed class Event {
    sealed class Exception : Event() {
        object NotEnoughCredits : Exception()
    }
}