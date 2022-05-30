package com.wing.tree.android.wordle.data.mapper

import com.wing.tree.android.wordle.data.datastore.settings.Settings
import com.wing.tree.android.wordle.domain.model.settings.Settings as DomainSettings

object SettingsMapper {
    fun Settings.toDomainModel() = object : DomainSettings {
        override val isHardMode: Boolean = getIsHardMode()
        override val vibrates: Boolean = getVibrates()
        override val isHighContrastMode: Boolean = getIsHighContrastMode()
    }
}