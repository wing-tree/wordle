package com.wing.tree.android.wordle.domain.model

interface Word : Iterable<Char> {
    val index: Int
    val value: String

    override fun iterator(): Iterator<Char> {
        return object : Iterator<Char> {
            private var index = 0

            override fun hasNext(): Boolean {
                return index <= value.lastIndex
            }

            override fun next(): Char {
                return value[index++]
            }
        }
    }

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