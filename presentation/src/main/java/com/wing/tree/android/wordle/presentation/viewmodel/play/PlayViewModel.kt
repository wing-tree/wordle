package com.wing.tree.android.wordle.presentation.viewmodel.play

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wing.tree.android.wordle.presentation.model.Letters

class PlayViewModel(application: Application) : AndroidViewModel(application) {
    private val _letters = MutableLiveData(Array(6) { Letters() })
    val letters: LiveData<Array<Letters>> get() = _letters

    private var _try = 0
    val `try`: Int get() = _try

    fun addLetter(letter: String) {
        _letters.value?.let { letters ->
            letters[`try`].addLetter(letter)

            _letters.value = letters
        }
    }

    fun removeLastLetter() {
        _letters.value?.let { letters ->
            letters[`try`].removeLastLetter()

            _letters.value = letters
        }
    }

    fun incrementTry() {
        ++_try
    }
}