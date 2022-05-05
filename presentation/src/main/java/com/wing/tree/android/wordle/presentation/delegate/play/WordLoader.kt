package com.wing.tree.android.wordle.presentation.delegate.play

import com.wing.tree.android.wordle.domain.model.Word

interface WordLoader {
    suspend fun load(index: Int): Word?
    suspend fun loadAtRandom(): Word?
}