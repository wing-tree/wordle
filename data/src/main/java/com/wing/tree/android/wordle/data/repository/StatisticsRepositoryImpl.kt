package com.wing.tree.android.wordle.data.repository

import androidx.datastore.core.DataStore
import com.wing.tree.android.wordle.data.datastore.statistics.Statistics
import com.wing.tree.android.wordle.data.mapper.GuessesMapper.toDataModel
import com.wing.tree.android.wordle.data.mapper.StatisticsMapper.toDomainModel
import com.wing.tree.android.wordle.domain.model.Guesses
import com.wing.tree.android.wordle.domain.model.Statistics as DomainStatistics
import com.wing.tree.android.wordle.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StatisticsRepositoryImpl @Inject constructor(private val dataStore: DataStore<Statistics>) : StatisticsRepository {
    override fun get(): Flow<DomainStatistics> {
        return dataStore.data.map { it.toDomainModel() }
    }

    override suspend fun update(statistics: DomainStatistics) {
        with(statistics) {
            dataStore.updateData {
                it.toBuilder()
                    .setMaximumWinStreak(maximumWinStreak)
                    .setPlayed(played)
                    .setWin(win)
                    .setWinningStreak(winningStreak)
                    .setGuesses(guesses.toDataModel())
                    .build()
            }
        }
    }
}