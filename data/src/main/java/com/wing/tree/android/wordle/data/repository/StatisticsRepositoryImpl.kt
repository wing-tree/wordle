package com.wing.tree.android.wordle.data.repository

import androidx.datastore.core.DataStore
import com.wing.tree.android.wordle.data.datastore.statistics.Guesses
import com.wing.tree.android.wordle.data.datastore.statistics.Statistics
import com.wing.tree.android.wordle.data.mapper.StatisticsMapper.toDomainModel
import com.wing.tree.android.wordle.domain.model.Result
import com.wing.tree.android.wordle.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.max
import com.wing.tree.android.wordle.domain.model.Statistics as DomainStatistics

class StatisticsRepositoryImpl @Inject constructor(private val dataStore: DataStore<Statistics>) : StatisticsRepository {
    override fun get(): Flow<DomainStatistics> {
        return dataStore.data.map { it.toDomainModel() }
    }

    override suspend fun update(result: Result, guess: Int) {
        dataStore.updateData {
            val maximumWinStreak: Int
            val played = it.played.inc()
            val winningStreak: Int
            val won: Int

            if (result is Result.Lose) {
                maximumWinStreak = it.maximumWinStreak
                winningStreak = 0
                won = it.won
            } else {
                winningStreak = it.winningStreak.inc()
                maximumWinStreak = max(winningStreak, it.maximumWinStreak)
                won = it.won.inc()
            }

            val guesses = if (result is Result.Lose) {
                it.guesses
            } else {
                with(Guesses.newBuilder(it.guesses)) {
                    when (guess) {
                        0 -> one = it.guesses.one.inc()
                        1 -> two = it.guesses.two.inc()
                        2 -> three = it.guesses.three.inc()
                        3 -> four = it.guesses.four.inc()
                        4 -> five = it.guesses.five.inc()
                        else -> sixOrMore = it.guesses.sixOrMore.inc()
                    }

                    build()
                }
            }

            it.toBuilder()
                .setMaximumWinStreak(maximumWinStreak)
                .setPlayed(played)
                .setWinningStreak(winningStreak)
                .setWon(won)
                .setGuesses(guesses)
                .build()
        }
    }
}