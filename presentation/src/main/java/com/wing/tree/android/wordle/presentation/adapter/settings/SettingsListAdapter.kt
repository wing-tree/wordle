package com.wing.tree.android.wordle.presentation.adapter.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewbinding.ViewBinding
import com.wing.tree.android.wordle.presentation.databinding.DividerBinding
import com.wing.tree.android.wordle.presentation.databinding.PreferenceBinding
import com.wing.tree.android.wordle.presentation.databinding.SwitchPreferenceBinding
import com.wing.tree.android.wordle.presentation.extention.gone
import com.wing.tree.android.wordle.presentation.extention.visible
import com.wing.tree.android.wordle.presentation.model.settings.Settings

class SettingsListAdapter : ListAdapter<Settings, SettingsListAdapter.ViewHolder<Settings>>(DiffCallback()) {
    sealed class ViewHolder<out T: Settings>(viewBinding: ViewBinding): RecyclerView.ViewHolder(viewBinding.root) {
        abstract fun bind(item: Settings)
    }

    inner class DividerViewHolder(viewBinding: DividerBinding) : ViewHolder<Settings.Divider>(viewBinding) {
        override fun bind(item: Settings) = Unit
    }

    inner class PreferenceViewHolder(private val viewBinding: PreferenceBinding) : ViewHolder<Settings.Preference>(viewBinding) {
        override fun bind(item: Settings) {
            if (item is Settings.Preference) {
                with(viewBinding) {
                    textViewPrimary.text = item.primaryText

                    item.drawable?.let {
                        imageView.visible()
                        imageView.setImageDrawable(it)
                    } ?: imageView.gone()

                    item.secondaryText?.let {
                        textViewSecondary.visible()
                        textViewSecondary.text = it
                    } ?: textViewSecondary.gone()


                    if (item.isClickable) {
                        root.setOnClickListener { item.onClick?.invoke(item) }
                    } else {
                        root.setOnClickListener(null)
                    }

                    root.isClickable = item.isClickable
                }
            }
        }
    }

    inner class SwitchPreferenceViewHolder(private val viewBinding: SwitchPreferenceBinding):
        SettingsListAdapter.ViewHolder<Settings.SwitchPreference>(viewBinding) {
        override fun bind(item: Settings) {
            if (item is Settings.SwitchPreference) {
                with(viewBinding) {
                    textViewPrimary.text = item.primaryText

                    item.secondaryText?.let {
                        textViewSecondary.visible()
                        textViewSecondary.text = it
                    } ?: textViewSecondary.gone()

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

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        with(recyclerView.itemAnimator) {
            if (this is SimpleItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Settings> {
        val inflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            ViewType.Divider.value -> {
                val viewBinding = DividerBinding.inflate(inflater, parent, false)

                DividerViewHolder(viewBinding)
            }
            ViewType.Preference.value -> {
                val viewBinding = PreferenceBinding.inflate(inflater, parent, false)

                PreferenceViewHolder(viewBinding)
            }
            ViewType.SwitchPreference.value -> {
                val viewBinding = SwitchPreferenceBinding.inflate(inflater, parent, false)

                SwitchPreferenceViewHolder(viewBinding)
            }
            else -> throw IllegalArgumentException("$viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<Settings>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is Settings.Divider -> ViewType.Divider.value
            is Settings.Preference -> ViewType.Preference.value
            is Settings.SwitchPreference -> ViewType.SwitchPreference.value
        }
    }

    enum class ViewType(val value: Int) {
        Divider(0), Preference(1), SwitchPreference(2)
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