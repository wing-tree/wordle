package com.wing.tree.android.wordle.domain.repository

import com.wing.tree.android.wordle.domain.model.Statistics
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
    fun get(): Flow<Statistics>
    suspend fun update(statistics: Statistics)
}