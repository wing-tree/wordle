package com.wing.tree.android.wordle.presentation.model.play

import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.presentation.mapper.PlayStateMapper.toPresentationModel
import com.wing.tree.wordle.core.constant.alphabet
import com.wing.tree.android.wordle.domain.model.playstate.Keyboard as DomainKeyboard
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class Keyboard {
    val alphabetKeys = Array(alphabet.size) { Key.Alphabet(alphabet[it]) }
    val runsAnimation = AtomicBoolean(false)

    fun getErasableAlphabetKeys(word: Word): List<Key.Alphabet> = alphabetKeys.filter(ErasePredicate(word))

    fun erase(word: Word) {
        val predicate = ErasePredicate(word)
        val seed = System.currentTimeMillis()
        val random = Random(seed)

        val shuffled = alphabetKeys.filter(predicate).shuffled(random)

        shuffled.take(3).forEach {
            it.erase()
        }
    }

    inner class ErasePredicate(val word: Word): (Key.Alphabet) -> Boolean {
        override fun invoke(alphabet: Key.Alphabet) = when {
            alphabet.letter in word.value -> false
            alphabet.state is Key.State.NotIn -> false
            else -> true
        }
    }

    companion object {
        fun from(keyboard: DomainKeyboard): Keyboard {
            return Keyboard().apply {
                keyboard.alphabets.forEachIndexed { index, alphabet ->
                    alphabetKeys[index] = alphabet.toPresentationModel()
                }
            }
        }
    }
}