package com.wing.tree.android.wordle.data.mapper

import com.wing.tree.android.wordle.data.datastore.statistics.Statistics
import com.wing.tree.android.wordle.data.mapper.GuessesMapper.toDomainModel
import com.wing.tree.android.wordle.domain.model.staticstics.Guesses
import com.wing.tree.android.wordle.domain.model.staticstics.Statistics as DomainStatistics

internal object StatisticsMapper {
    fun Statistics.toDomainModel() = object : DomainStatistics {
        val statistics = this@toDomainModel

        override val maximumWinStreak: Int = statistics.maximumWinStreak
        override val played: Int = statistics.played
        override val winningStreak: Int = statistics.winningStreak
        override val won: Int = statistics.won
        override val guesses: Guesses = statistics.guesses.toDomainModel()
    }
}