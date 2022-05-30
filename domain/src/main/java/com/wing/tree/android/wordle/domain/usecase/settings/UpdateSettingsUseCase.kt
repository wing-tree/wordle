package com.wing.tree.android.wordle.domain.usecase.settings

import com.wing.tree.android.wordle.domain.model.settings.Settings
import com.wing.tree.android.wordle.domain.repository.SettingsRepository
import com.wing.tree.android.wordle.domain.usecase.core.CoroutineUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository,
    @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
) : CoroutineUseCase<UpdateSettingsUseCase.Parameter, Unit>(coroutineDispatcher) {
    override suspend fun execute(parameter: Parameter) {
        repository.update(parameter.type, parameter.value)
    }

    data class Parameter(val type: Settings.Type, val value: Boolean)
}