package com.wing.tree.android.wordle.domain.repository

import com.wing.tree.android.wordle.domain.model.Word

interface WordRepository {
    suspend fun contain(letters: String) : Boolean
    suspend fun count(): Int
    suspend fun get(index: Int) : Word
}