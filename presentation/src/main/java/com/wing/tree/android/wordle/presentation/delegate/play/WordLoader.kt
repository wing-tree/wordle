package com.wing.tree.android.wordle.presentation.delegate.play

import androidx.annotation.MainThread
import com.wing.tree.android.wordle.domain.model.Word

interface WordLoader {
    suspend fun load(@MainThread onSuccess: (Word) -> Unit, @MainThread onFailure: (Throwable) -> Unit)
}