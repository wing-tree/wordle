package com.wing.tree.android.wordle.domain.usecase.billing

import com.wing.tree.android.wordle.domain.repository.PreferencesRepository
import com.wing.tree.android.wordle.domain.usecase.core.CoroutineUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class PurchaseCreditsUseCase @Inject constructor(
    private val repository: PreferencesRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<Int, Unit>(coroutineDispatcher) {
    override suspend fun execute(parameter: Int) {
        repository.purchaseCredits(parameter)
    }
}