package com.wing.tree.android.wordle.presentation.model.play

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class PlayResult : Parcelable {
    data class Lose(
        val letters: String,
        val round: Int,
        val states: List<Int>,
        val word: String
    ) : PlayResult()

    data class Win(
        val round: Int,
        val word: String
    ) : PlayResult()
}