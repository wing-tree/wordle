package com.wing.tree.android.wordle.domain.usecase.word

import com.wing.tree.android.wordle.domain.model.Word
import com.wing.tree.android.wordle.domain.repository.WordRepository
import com.wing.tree.android.wordle.domain.usecase.core.CoroutineUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import com.wing.tree.android.wordle.domain.usecase.core.Result
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetWordUseCase @Inject constructor(
    private val repository: WordRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<GetWordUseCase.Parameter, Word>(coroutineDispatcher) {
    override suspend fun execute(parameter: Parameter): Word {
        return when(parameter) {
            is Parameter.Index -> repository.get(parameter.value)
            is Parameter.Random -> repository.random()
        }
    }

    sealed class Parameter {
        data class Index(val value: Int) : Parameter()
        object Random : Parameter()
    }
}