package com.wing.tree.android.wordle.presentation.model.play

import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.presentation.constant.Attempt
import com.wing.tree.android.wordle.presentation.constant.Word.LENGTH
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class Board {
    private val attempt = AtomicInteger(0)
    private val maximumAttempt = AtomicInteger(Attempt.MAXIMUM)
    val attemptIncremented = AtomicBoolean(false)

    private val _lettersList = mutableListOf<Letters>()
    val lettersList: List<Letters> get() = _lettersList

    val currentLetters: Letters get() = lettersList[attempt.get()]

    val lettersMatched = lettersWithState(State.Included.Matched())

    private fun lettersWithState(vararg state: State) = lettersList.flatten().filter { state.contains(it.state) }

    fun getNotMatchedYetLetters(word: Word): List<Letter> {
        val matchedPositions = lettersMatched.map { it.position }

        return mutableListOf<Letter>().apply {
            word.word.forEachIndexed { index, letter ->
                if (matchedPositions.contains(index).not()) {
                    add(Letter(index, letter))
                }
            }
        }
    }

    val notUnknownLetters: List<Letter> get() = lettersList.flatten().filter { it.state.notUnknown }

    init {
        repeat(Attempt.MAXIMUM) {
            _lettersList.add(Letters())
        }
    }

    fun add(letter: String) {
        with(currentLetters) {
            if (length < LENGTH) {
                add(letter)
            }
        }
    }

    fun removeAt(attempt: Int, index: Int) {
        try {
            lettersList[attempt].removeAt(index)
        } catch (e: ArrayIndexOutOfBoundsException) {
            Timber.e(e)
        }
    }

    fun removeLast() {
        currentLetters.removeLast()
    }

    fun submit() {
        currentLetters.submit()
    }

    val attemptExceeded: Boolean get() = attempt.get() >= maximumAttempt.get().dec()


    fun addAttempt() {
        if (attemptIncremented.compareAndSet(false, true)) {
            Timber.d("${maximumAttempt.incrementAndGet()}")
            incrementAttempt()
            _lettersList.add(Letters())
        }
    }

    fun incrementAttempt() {
        Timber.d("${attempt.incrementAndGet()}")
    }
}