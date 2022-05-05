package com.wing.tree.android.wordle.domain.repository

import com.wing.tree.android.wordle.domain.model.Word

interface WordRepository {
    suspend fun contains(letters: String) : Boolean
    suspend fun get(index: Int) : Word
    suspend fun random(): Word
}