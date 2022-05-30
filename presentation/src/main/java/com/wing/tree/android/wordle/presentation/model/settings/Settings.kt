package com.wing.tree.android.wordle.presentation.model.settings

import android.graphics.drawable.Drawable

sealed class Settings {
    abstract val id: Long
    abstract val isCheckable: Boolean

    data class SwitchPreference(
        override val id: Long,
        override val isCheckable: Boolean = true,
        val body: String,
        val drawable: Drawable? = null,
        val isChecked: Boolean,
        val summary: String? = null
    ) : Settings() {
        private var _onCheckedChange: ((isChecked: Boolean) -> Unit)? = null
        val onCheckedChange: ((Boolean) -> Unit)? get() = _onCheckedChange

        fun setOnCheckedChange(onCheckedChange: (isChecked: Boolean) -> Unit) {
            this._onCheckedChange = onCheckedChange
        }
    }
}