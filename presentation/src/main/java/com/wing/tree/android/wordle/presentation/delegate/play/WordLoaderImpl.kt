package com.wing.tree.android.wordle.presentation.delegate.play

import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.domain.usecase.core.getOrNull
import com.wing.tree.android.wordle.domain.usecase.word.GetWordUseCase
import com.wing.tree.android.wordle.domain.usecase.word.GetWordUseCase.Parameter

class WordLoaderImpl(private val getWordUseCase: GetWordUseCase) : WordLoader {
    override suspend fun load(index: Int): Word? {
        return getWordUseCase(Parameter.Index(index)).getOrNull()
    }

    override suspend fun loadAtRandom(): Word? {
        return getWordUseCase(Parameter.Random).getOrNull()
    }
}