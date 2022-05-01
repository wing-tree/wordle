package com.wing.tree.android.wordle.domain.usecase.billing

import com.wing.tree.android.wordle.domain.repository.PreferencesRepository
import com.wing.tree.android.wordle.domain.usecase.core.FlowUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import com.wing.tree.android.wordle.domain.usecase.core.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetGoldUseCase @Inject constructor(
    private val repository: PreferencesRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, Int>(coroutineDispatcher) {
    override fun execute(parameter: Unit): Flow<Result<Int>> {
        return repository.getGold().map {
            try {
                Result.Success(it)
            } catch (throwable: Throwable) {
                Result.Error(throwable)
            }
        }
    }
}