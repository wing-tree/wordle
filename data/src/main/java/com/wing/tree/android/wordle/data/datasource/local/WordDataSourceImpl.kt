package com.wing.tree.android.wordle.data.datasource.local

import com.wing.tree.android.wordle.data.database.Database
import com.wing.tree.android.wordle.data.entity.Word
import javax.inject.Inject

class WordDataSourceImpl @Inject constructor(database: Database) : WordDataSource {
    private val dao = database.wordDao()

    override suspend fun count(): Int {
        return dao.count()
    }

    override suspend fun get(index: Int): Word {
        return dao.get(index)
    }

    override suspend fun get(letters: String): Word? {
        return dao.get(letters)
    }

    override suspend fun insertAll(words: List<Word>) {
        dao.insertAll(words)
    }

    override suspend fun random(): Word {
        return dao.random()
    }
}