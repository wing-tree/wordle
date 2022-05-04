package com.wing.tree.android.wordle.domain.model.playstate

interface PlayBoard {
    val round: Int
    val maximumRound: Int
    val lines: List<Line>
}