package com.wing.tree.android.wordle.domain.usecase.billing

import com.wing.tree.android.wordle.domain.repository.PreferencesRepository
import com.wing.tree.android.wordle.domain.usecase.core.FlowUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import com.wing.tree.android.wordle.domain.usecase.core.NoParameterFlowUseCase
import com.wing.tree.android.wordle.domain.usecase.core.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCreditsUseCase @Inject constructor(
    private val repository: PreferencesRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : NoParameterFlowUseCase<Int>(coroutineDispatcher) {
    override fun execute(): Flow<Int> {
        return repository.getCredits()
    }
}