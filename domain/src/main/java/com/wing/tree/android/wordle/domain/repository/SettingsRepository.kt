package com.wing.tree.android.wordle.domain.repository

import com.wing.tree.android.wordle.domain.model.settings.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun get(): Flow<Settings>
    suspend fun update(type: Settings.Type, value: Boolean)
}