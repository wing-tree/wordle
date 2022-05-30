package com.wing.tree.android.wordle.presentation.adapter.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.wing.tree.android.wordle.presentation.databinding.SwitchPreferenceItemBinding
import com.wing.tree.android.wordle.presentation.model.settings.Settings

class SettingsListAdapter : ListAdapter<Settings, SettingsListAdapter.ViewHolder<Settings>>(DiffCallback()) {
    sealed class ViewHolder<out T: Settings>(viewBinding: ViewBinding): RecyclerView.ViewHolder(viewBinding.root) {
        abstract fun bind(item: Settings)
    }

    inner class SwitchPreferenceViewHolder(private val viewBinding: SwitchPreferenceItemBinding):
        SettingsListAdapter.ViewHolder<Settings.SwitchPreference>(viewBinding) {
        override fun bind(item: Settings) {
            if (item is Settings.SwitchPreference) {
                with(viewBinding) {
                    textViewBody.text = item.body
                    textViewSummary.text = item.summary
                    switchCompat.isChecked = item.isChecked

                    switchCompat.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (buttonView.isPressed) {
                            item.onCheckedChange?.invoke(isChecked)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Settings> {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = SwitchPreferenceItemBinding.inflate(inflater, parent, false)

        return SwitchPreferenceViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder<Settings>, position: Int) {
        holder.bind(getItem(position))
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