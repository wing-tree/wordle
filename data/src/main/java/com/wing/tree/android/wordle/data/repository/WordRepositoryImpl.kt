package com.wing.tree.android.wordle.data.repository

import com.wing.tree.android.wordle.data.datasource.local.WordDataSource
import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.domain.repository.WordRepository
import com.wing.tree.android.wordle.domain.util.notNull
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(private val dataSource: WordDataSource): WordRepository {
    override suspend fun contains(letters: String): Boolean {
        return dataSource.get(letters).notNull
    }

    override suspend fun count(): Int {
        return dataSource.count()
    }

    override suspend fun get(index: Int): Word {
        return dataSource.get(index)
    }
}