package com.wing.tree.android.wordle.domain.model

interface Guesses {
    val one: Int
    val two: Int
    val three: Int
    val four: Int
    val five: Int
    val six: Int

    companion object {
        val Default = object : Guesses {
            override val one: Int = 0
            override val two: Int = 0
            override val three: Int = 0
            override val four: Int = 0
            override val five: Int = 0
            override val six: Int = 0
        }
    }
}