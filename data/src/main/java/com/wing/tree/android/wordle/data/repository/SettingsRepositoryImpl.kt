package com.wing.tree.android.wordle.data.repository

import androidx.datastore.core.DataStore
import com.wing.tree.android.wordle.data.datastore.settings.Settings
import com.wing.tree.android.wordle.data.mapper.SettingsMapper.toDomainModel
import com.wing.tree.android.wordle.domain.model.settings.Settings as DomainSettings
import com.wing.tree.android.wordle.domain.model.settings.Settings.Type
import com.wing.tree.android.wordle.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(private val dataStore: DataStore<Settings>) : SettingsRepository {
    override fun get(): Flow<DomainSettings> {
        return dataStore.data.map { it.toDomainModel() }
    }

    override suspend fun update(
        type: Type,
        value: Boolean
    ) {
        dataStore.updateData {
            val builder = it.toBuilder()

            when(type) {
                Type.HardMode -> builder.isHardMode = value
                Type.Vibrates -> builder.vibrates = value
                Type.HighContrastMode -> builder.isHighContrastMode = value
            }

            builder.build()
        }
    }
}