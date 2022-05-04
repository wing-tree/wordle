package com.wing.tree.android.wordle.presentation.delegate.play

import androidx.annotation.MainThread
import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.presentation.model.play.Line

interface LettersChecker {
    suspend fun submit(
        word: Word,
        line: Line,
        @MainThread onFailure: (Throwable) -> Unit,
        @MainThread onSuccess: (Line) -> Unit
    )
}