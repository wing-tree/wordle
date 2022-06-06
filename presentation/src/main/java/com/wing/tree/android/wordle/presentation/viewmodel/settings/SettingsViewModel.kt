package com.wing.tree.android.wordle.presentation.viewmodel.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.wing.tree.android.wordle.domain.model.settings.Settings
import com.wing.tree.android.wordle.domain.usecase.core.getOrDefault
import com.wing.tree.android.wordle.domain.usecase.settings.GetSettingsUseCase
import com.wing.tree.android.wordle.domain.usecase.settings.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    application: Application
) : AndroidViewModel(application) {
    fun updateHardMode(value: Boolean) {
        viewModelScope.launch {
            val parameter = UpdateSettingsUseCase.Parameter(
                Settings.Type.HardMode,
                value
            )

            updateSettingsUseCase.invoke(parameter)
        }
    }

    fun updateVibrates(value: Boolean) {
        viewModelScope.launch {
            val parameter = UpdateSettingsUseCase.Parameter(
                Settings.Type.Vibrates,
                value
            )

            updateSettingsUseCase.invoke(parameter)
        }
    }

    fun updateHighContrastMode(value: Boolean) {
        viewModelScope.launch {
            val parameter = UpdateSettingsUseCase.Parameter(
                Settings.Type.HighContrastMode,
                value
            )

            updateSettingsUseCase.invoke(parameter)
        }
    }
}