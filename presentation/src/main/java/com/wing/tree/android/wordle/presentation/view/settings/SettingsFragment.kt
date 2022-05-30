package com.wing.tree.android.wordle.presentation.view.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.adapter.settings.SettingsListAdapter
import com.wing.tree.android.wordle.presentation.databinding.FragmentSettingsBinding
import com.wing.tree.android.wordle.presentation.model.settings.Settings
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    private val viewModel by viewModels<SettingsViewModel>()

    private val settingsListAdapter = SettingsListAdapter()
    private val concatAdapter = ConcatAdapter(settingsListAdapter)

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        viewModel.settings.observe(viewLifecycleOwner) { settings ->

            val list = listOf(
                Settings.SwitchPreference(
                    id = 0L,
                    body = getString(R.string.hard_mode),
                    isChecked = settings.isHardMode
                ).apply {
                    setOnCheckedChange { viewModel.updateHardMode(it) }
                },
                Settings.SwitchPreference(
                    id = 1L,
                    body = getString(R.string.vibrate),
                    isChecked = settings.vibrates
                ).apply {
                    setOnCheckedChange { viewModel.updateVibrates(it) }
                },
                Settings.SwitchPreference(
                    id = 2L,
                    body = getString(R.string.high_contrast_mode),
                    isChecked = settings.isHighContrastMode
                ).apply {
                    setOnCheckedChange { viewModel.updateHighContrastMode(it) }
                }
            )

            settingsListAdapter.submitList(list)
        }
    }

    override fun bind(viewBinding: FragmentSettingsBinding) {
        viewBinding.root.apply {
            adapter = concatAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}