package com.wing.tree.android.wordle.presentation.model.play

import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.presentation.constant.Attempt
import com.wing.tree.android.wordle.presentation.constant.Word.LENGTH
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

class Board {
    val board = arrayListOf<Letters>()
    val attempt = AtomicInteger(0)
    val currentLetters: Letters
        get() = board[attempt.get()]

    private val correctLettersInRightPlace: List<Letter> // 이미 맞춰진, 정확한.
        get() = board.flatMap { it }.filter { it.state == State.In.CorrectSpot() }

    // 상태기반 필터로직.

    // 아니면 목적 지향적 추상화. dart target 등..
//    val notMatchedYetLetters: List<Letter>
//        get() = run {
//            val w = word.word // 이걸로 레터 구성, 매치드 레터의 인덱스 위치의 값을 제거. 리스트 반환
//            val arr = mutableListOf<Letter>()
//            val mi = correctLettersInRightPlace.map { it.position }
//
//            w.forEachIndexed { index, c ->
//                if (mi.contains(index).not()) {
//                    arr.add(Letter(index, c))
//                }
//            }
//
//            arr
//        }

    val notUnknownLetters: List<Letter> get() = board.flatten().filter { it.state.notUnknown }

    init {
        repeat(Attempt.MAXIMUM) {
            board.add(Letters())
        }
    }

    fun add(letter: String) {
        currentLetters.add(letter)
        with(currentLetters) {
            if (length < LENGTH) {
                add(letter)
            }
        }
    }

    fun incrementAttempt() {
        Timber.d("${attempt.incrementAndGet()}")
    }
}