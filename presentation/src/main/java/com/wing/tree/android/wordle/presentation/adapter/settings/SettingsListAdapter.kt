package com.wing.tree.android.wordle.presentation.adapter.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.wing.tree.android.wordle.presentation.databinding.PreferenceCategoryItemBinding
import com.wing.tree.android.wordle.presentation.databinding.PreferenceItemBinding
import com.wing.tree.android.wordle.presentation.databinding.SpaceItemBinding
import com.wing.tree.android.wordle.presentation.databinding.SwitchPreferenceItemBinding
import com.wing.tree.android.wordle.presentation.model.settings.Settings

class SettingsListAdapter(val k: Int) : ListAdapter<Settings, SettingsListAdapter.ViewHolder<Settings>>(DiffCallback()) {
    sealed class ViewHolder<out T: Settings>(viewBinding: ViewBinding): RecyclerView.ViewHolder(viewBinding.root) {
        abstract fun bind(item: Settings)
    }

    inner class PreferenceViewHolder(viewBinding: PreferenceItemBinding): SettingsListAdapter.ViewHolder<Settings.Preference>(viewBinding) {
        override fun bind(item: Settings) {
            if (item is Settings.Preference) {

            }
        }
    }

    inner class PreferenceCategoryViewHolder(viewBinding: PreferenceCategoryItemBinding): SettingsListAdapter.ViewHolder<Settings.PreferenceCategory>(viewBinding) {
        override fun bind(item: Settings) {
            if (item is Settings.PreferenceCategory) {

            }
        }
    }

    inner class SpaceViewHolder(viewBinding: SpaceItemBinding): SettingsListAdapter.ViewHolder<Settings.Space>(viewBinding) {
        override fun bind(item: Settings) {
            if (item is Settings.Space) {

            }
        }
    }

    inner class SwitchPreferenceViewHolder(viewBinding: SwitchPreferenceItemBinding): SettingsListAdapter.ViewHolder<Settings.SwitchPreference>(viewBinding) {
        override fun bind(item: Settings) {
            if (item is Settings.SwitchPreference) {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Settings> {
        return PreferenceViewHolder(PreferenceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder<Settings>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    class DiffCallback: DiffUtil.ItemCallback<Settings>() {
        override fun areItemsTheSame(oldItem: Settings, newItem: Settings): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Settings, newItem: Settings): Boolean {
            return oldItem == newItem
        }
    }
}