package com.wing.tree.android.wordle.domain.model

interface Word {
    val index: Int
    val value: String

    companion object {
        val Sorry = object : Word {
            override val index: Int = 593
            override val value: String = "sorry"
        }

        fun from(word: Word) = object : Word {
            override val index: Int = word.index
            override val value: String = word.value
        }
    }
}