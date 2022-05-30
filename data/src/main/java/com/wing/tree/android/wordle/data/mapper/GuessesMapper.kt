package com.wing.tree.android.wordle.data.mapper

import com.wing.tree.android.wordle.data.datastore.statistics.Guesses
import com.wing.tree.android.wordle.domain.model.staticstics.Guesses as DomainGuesses

object GuessesMapper {
    fun Guesses.toDomainModel() = object : DomainGuesses {
        override val one: Int = getOne()
        override val two: Int = getTwo()
        override val three: Int = getThree()
        override val four: Int = getFour()
        override val five: Int = getFive()
        override val sixOrMore: Int = getSixOrMore()
    }
}