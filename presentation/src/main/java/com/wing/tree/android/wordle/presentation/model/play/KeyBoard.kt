package com.wing.tree.android.wordle.presentation.model.play

import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.presentation.util.alphabet
import java.util.*

class KeyBoard {
    val alphabets = Array(alphabet.size) { Key.Alphabet(alphabet[it]) }

    fun excludeKeys(word: Word) {
        val filtered = alphabets.filter(ErasePredicate(word))
        val seed = System.currentTimeMillis()
        val shuffled = filtered.shuffled(Random(seed))

        shuffled.take(3).forEach {
            it.exclude()
        }
    }

    class ErasePredicate(val word: Word): (Key.Alphabet) -> Boolean {
        override fun invoke(alphabet: Key.Alphabet) = when {
            alphabet.state == Key.State.NotIn() -> false
            alphabet.letter in word.value -> false
            else -> true
        }
    }
}