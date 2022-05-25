package com.wing.tree.android.wordle.presentation.delegate.play

import com.wing.tree.android.wordle.presentation.model.play.Line

interface LettersChecker {
    suspend fun submit(answer: String, line: Line): Result<Line>
}