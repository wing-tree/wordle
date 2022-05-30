package com.wing.tree.android.wordle.domain.repository

import com.wing.tree.android.wordle.domain.model.Result
import com.wing.tree.android.wordle.domain.model.staticstics.Statistics
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
    fun get(): Flow<Statistics>
    suspend fun update(result: Result, guess: Int)
}