package com.wing.tree.android.wordle.presentation.delegate.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class KeyboardActionDelegateImpl : KeyboardActionDelegate {
    private val _keyboardEnabled = MutableLiveData(false)
    override val keyboardEnabled: LiveData<Boolean> get() = _keyboardEnabled

    private val _returnKeyEnabled = MutableLiveData(false)
    override val returnKeyEnabled: LiveData<Boolean> get() = _returnKeyEnabled

    override fun disableKeyboard() {
        _keyboardEnabled.value = false
    }

    override fun disableReturnKey() {
        _returnKeyEnabled.value = false
    }

    override fun enableKeyboard() {
        _keyboardEnabled.value = true
    }

    override fun enableReturnKey() {
        _returnKeyEnabled.value = true
    }
}