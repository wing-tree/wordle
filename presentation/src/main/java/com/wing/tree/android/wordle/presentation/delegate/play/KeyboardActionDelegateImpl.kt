package com.wing.tree.android.wordle.presentation.delegate.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class KeyboardActionDelegateImpl : KeyboardActionDelegate {
    private val _keyboardEnabled = MutableLiveData(false)
    override val keyboardEnabled: LiveData<Boolean> get() = _keyboardEnabled

    override fun disableKeyboard() {
        _keyboardEnabled.value = false
    }

    override fun enableKeyboard() {
        _keyboardEnabled.value = true
    }
}