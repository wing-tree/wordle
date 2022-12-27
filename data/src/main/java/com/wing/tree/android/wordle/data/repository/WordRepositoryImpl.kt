package com.wing.tree.android.wordle.data.repository

import com.wing.tree.android.wordle.data.datasource.local.WordDataSource
import com.wing.tree.android.wordle.data.entity.Word as Entity
import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.domain.repository.WordRepository
import com.wing.tree.android.wordle.domain.util.isNotNull
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(private val dataSource: WordDataSource): WordRepository {
    override suspend fun contains(letters: String): Boolean {
       return runCatching {
            dataSource.get(letters).isNotNull()
        }.getOrDefault(false)
    }

    override suspend fun get(index: Int): Word {
        return runCatching {
            dataSource.get(index)
        }.getOrThrow()
    }

    override suspend fun random(): Word {
        return runCatching {
            dataSource.random()
        }.getOrThrow()
    }

    override suspend fun insertAll(words: List<String>) {
        dataSource.insertAll(words.map { Entity(value = it) })
    }
}