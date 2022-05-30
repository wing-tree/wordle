package com.wing.tree.android.wordle.domain.model.staticstics

interface Statistics {
    val maximumWinStreak: Int
    val played: Int
    val winningStreak: Int
    val won: Int
    val guesses: Guesses

    companion object {
        val Default = object : Statistics {
            override val maximumWinStreak: Int = 0
            override val played: Int = 0
            override val winningStreak: Int = 0
            override val won: Int = 0
            override val guesses: Guesses = Guesses.Default
        }
    }
}