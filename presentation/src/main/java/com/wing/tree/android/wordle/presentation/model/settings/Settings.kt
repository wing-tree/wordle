package com.wing.tree.android.wordle.presentation.model.settings

import android.graphics.drawable.Drawable

sealed class Settings {
    abstract val id: Long

    data class Preference(
        override val id: Long,
        val drawable: Drawable?,
        val isClickable: Boolean = true,
        val primaryText: String,
        val secondaryText: String? = null
    ) : Settings() {
        private var _onClick: ((Preference) -> Unit)? = null
        val onClick: ((Preference) -> Unit)? get() = _onClick

        fun setOnClick(onClick: (Preference) -> Unit) {
            _onClick = onClick
        }
    }

    data class SwitchPreference(
        override val id: Long,
        val drawable: Drawable? = null,
        val isChecked: Boolean,
        val isCheckable: Boolean = true,
        val primaryText: String,
        val secondaryText: String? = null
    ) : Settings() {
        private var _onCheckedChange: ((isChecked: Boolean) -> Unit)? = null
        val onCheckedChange: ((Boolean) -> Unit)? get() = _onCheckedChange

        fun setOnCheckedChange(onCheckedChange: (isChecked: Boolean) -> Unit) {
            _onCheckedChange = onCheckedChange
        }
    }

    data class Divider(override val id: Long) : Settings()
}