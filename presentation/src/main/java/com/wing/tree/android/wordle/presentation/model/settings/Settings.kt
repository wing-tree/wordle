package com.wing.tree.android.wordle.presentation.model.settings

import android.graphics.drawable.Drawable

sealed class Settings {
    abstract val id: Long
    abstract val isClickable: Boolean

    data class Preference(
        override val id: Long,
        override val isClickable: Boolean = true,
        val body: String,
        val drawable: Drawable?,
        val onClick: (Preference) -> Unit,
        val summary: String
    ) : Settings()

    data class PreferenceCategory(
        override val id: Long,
        override val isClickable: Boolean = true,
        val category: String
    ) : Settings()

    data class Space(
        override val id: Long,
        override var isClickable: Boolean = false,
    ) : Settings()

    data class SwitchPreference(
        override val id: Long,
        override val isClickable: Boolean = true,
        val body: String,
        val drawable: Drawable?,
        val isChecked: Boolean,
        val onCheckedChange: (isChecked: Boolean) -> Unit,
        val summary: String
    ) : Settings()
}