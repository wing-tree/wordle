package com.wing.tree.android.wordle.domain.model

interface Statistics {
    val maximumWinStreak: Int
    val played: Int
    val win: Int
    val winningStreak: Int
    val guesses: Guesses
}