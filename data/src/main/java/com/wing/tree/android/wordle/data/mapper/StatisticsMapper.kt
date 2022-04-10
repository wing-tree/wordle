package com.wing.tree.android.wordle.data.mapper

import com.wing.tree.android.wordle.data.datastore.statistics.Statistics
import com.wing.tree.android.wordle.data.mapper.GuessesMapper.toDomainModel
import com.wing.tree.android.wordle.domain.model.Guesses
import com.wing.tree.android.wordle.domain.model.Statistics as DomainStatistics

internal object StatisticsMapper {
    fun Statistics.toDomainModel() = object : DomainStatistics {
        val statistics = this@toDomainModel

        override val maximumWinStreak: Int
            get() = statistics.maximumWinStreak
        override val played: Int
            get() = statistics.played
        override val win: Int
            get() = statistics.win
        override val winningStreak: Int
            get() = statistics.winningStreak
        override val guesses: Guesses
            get() = statistics.guesses.toDomainModel()
    }
}