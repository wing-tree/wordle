package com.wing.tree.android.wordle.presentation.delegate.play

import androidx.annotation.MainThread
import com.wing.tree.android.wordle.presentation.model.play.Letters

interface LettersChecker {
    suspend fun submit(
        word: String,
        letters: Letters,
        @MainThread onError: (Throwable) -> Unit,
        @MainThread onSuccess: (Letters) -> Unit
    )
}