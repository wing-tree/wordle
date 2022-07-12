package com.wing.tree.android.wordle.presentation.model.play

import com.wing.tree.android.wordle.presentation.mapper.PlayStateMapper.toPresentationModel
import com.wing.tree.wordle.core.constant.alphabet
import com.wing.tree.android.wordle.domain.model.playstate.Keyboard as DomainKeyboard
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class Keyboard {
    private val eraseCount = 3

    val alphabets = Array(alphabet.size) { Key.Alphabet(alphabet[it]) }
    val runsAnimation = AtomicBoolean(false)

    private fun erasable(answer: String): List<Key.Alphabet> = alphabets.filter(Predicate(answer))

    fun erase(answer: String) {
        val seed = System.currentTimeMillis()
        val random = Random(seed)
        val shuffled = erasable(answer).shuffled(random)

        shuffled.take(eraseCount).forEach {
            it.erase()
        }
    }

    inner class Predicate(private val answer: String): (Key.Alphabet) -> Boolean {
        override fun invoke(alphabet: Key.Alphabet) = when {
            alphabet.letter in answer -> false
            alphabet.state is Key.State.NotIn -> false
            else -> true
        }
    }

    companion object {
        fun from(keyboard: DomainKeyboard): Keyboard {
            return Keyboard().apply {
                keyboard.alphabets.forEachIndexed { index, alphabet ->
                    alphabets[index] = alphabet.toPresentationModel()
                }
            }
        }
    }
}