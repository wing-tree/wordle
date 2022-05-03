package com.wing.tree.android.wordle.domain.model.playstate

interface PlayBoard {
    val round: Int
    val maximumRound: Int
    val isRoundAdded: Boolean
    val lines: List<Line>
}