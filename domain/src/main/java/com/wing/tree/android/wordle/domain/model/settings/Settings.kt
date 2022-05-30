package com.wing.tree.android.wordle.domain.model.settings

interface Settings {
    val isHardMode: Boolean
    val vibrates: Boolean
    val isHighContrastMode: Boolean

    enum class Type {
        HardMode,
        Vibrates,
        HighContrastMode
    }

    companion object {
        val Default = object : Settings {
            override val isHardMode: Boolean = false
            override val vibrates: Boolean = true
            override val isHighContrastMode: Boolean = false
        }
    }

}