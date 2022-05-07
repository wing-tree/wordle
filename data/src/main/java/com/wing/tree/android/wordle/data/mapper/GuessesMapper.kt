package com.wing.tree.android.wordle.data.mapper

import com.wing.tree.android.wordle.data.datastore.statistics.Guesses
import com.wing.tree.android.wordle.domain.model.Guesses as DomainGuesses

object GuessesMapper {
    fun Guesses.toDomainModel() = object : DomainGuesses {
        val guesses = this@toDomainModel

        override val one: Int
            get() = guesses.one
        override val two: Int
            get() = guesses.two
        override val three: Int
            get() = guesses.three
        override val four: Int
            get() = guesses.four
        override val five: Int
            get() = guesses.five
        override val sixOrMore: Int
            get() = guesses.sixOrMore
    }
}