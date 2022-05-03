package com.wing.tree.android.wordle.domain.model.playstate

import com.wing.tree.android.wordle.domain.model.Word

interface PlayState {
    val keyboard: Keyboard
    val playBoard: PlayBoard
    val word: Word
}