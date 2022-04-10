package com.wing.tree.android.wordle.data.datasource.local

import com.wing.tree.android.wordle.data.entity.Word

interface WordDataSource {
    suspend fun count(): Int
    suspend fun get(index: Int): Word
    suspend fun get(letters: String): Word?
}