package com.wing.tree.android.wordle.presentation.util

import androidx.lifecycle.MutableLiveData

internal fun <T: Any> MutableLiveData<T>.setValueAfter(block: T.() -> Unit) {
    value?.let {
        block(it)
        value = it
    }
}