package com.wing.tree.android.wordle.domain.usecase.settings

import com.wing.tree.android.wordle.domain.model.settings.Settings
import com.wing.tree.android.wordle.domain.repository.SettingsRepository
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import com.wing.tree.android.wordle.domain.usecase.core.NoParameterFlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : NoParameterFlowUseCase<Settings>(coroutineDispatcher) {
    override fun execute(): Flow<Settings> {
        return repository.get()
    }
}