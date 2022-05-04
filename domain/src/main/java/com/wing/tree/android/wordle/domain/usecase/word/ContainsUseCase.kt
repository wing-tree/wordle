package com.wing.tree.android.wordle.domain.usecase.word

import com.wing.tree.android.wordle.domain.repository.WordRepository
import com.wing.tree.android.wordle.domain.usecase.core.CoroutineUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import com.wing.tree.android.wordle.domain.usecase.core.Result
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ContainsUseCase @Inject constructor(
    private val repository: WordRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<String, Boolean>(coroutineDispatcher) {
    override suspend fun execute(parameter: String): Boolean {
        return repository.contains(parameter)
    }
}