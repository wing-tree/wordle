package com.wing.tree.android.wordle.presentation.delegate.play

import androidx.lifecycle.LiveData

interface KeyboardActionDelegate {
    val keyboardEnabled: LiveData<Boolean>
    val returnKeyEnabled: LiveData<Boolean>

    fun disableKeyboard()
    fun disableReturnKey()
    fun enableKeyboard()
    fun enableReturnKey()
}