package com.wing.tree.android.wordle.data.mapper

import com.wing.tree.android.wordle.data.datastore.statistics.Guesses
import com.wing.tree.android.wordle.domain.model.Guesses as DomainGuesses

object GuessesMapper {
    fun Guesses.toDomainModel() = object : DomainGuesses {
        val guesses = this@toDomainModel

        override val one: Int = guesses.one
        override val two: Int = guesses.two
        override val three: Int = guesses.three
        override val four: Int = guesses.four
        override val five: Int = guesses.five
        override val sixOrMore: Int = guesses.sixOrMore
    }
}