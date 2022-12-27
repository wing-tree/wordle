package com.wing.tree.android.wordle.domain.usecase.word

import com.wing.tree.android.wordle.domain.repository.WordRepository
import com.wing.tree.android.wordle.domain.usecase.core.CoroutineUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class InsertWordsUseCase @Inject constructor(
    private val repository: WordRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<List<String>, Unit>(coroutineDispatcher) {
    override suspend fun execute(parameter: List<String>) {
        return repository.insertAll(parameter)
    }
}