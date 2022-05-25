package com.wing.tree.android.wordle.domain.model.playstate

interface PlayBoard {
    val lastRound: Int
    val lines: List<Line>
    val round: Int
}