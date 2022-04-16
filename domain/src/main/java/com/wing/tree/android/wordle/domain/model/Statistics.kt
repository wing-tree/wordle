package com.wing.tree.android.wordle.domain.model

interface Statistics {
    val maximumWinStreak: Int
    val played: Int
    val winningStreak: Int
    val won: Int
    val guesses: Guesses
}