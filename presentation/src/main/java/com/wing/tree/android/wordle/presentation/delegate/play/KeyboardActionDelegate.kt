package com.wing.tree.android.wordle.presentation.delegate.play

import androidx.lifecycle.LiveData

interface KeyboardActionDelegate {
    val keyboardEnabled: LiveData<Boolean>

    fun disableKeyboard()
    fun enableKeyboard()
}