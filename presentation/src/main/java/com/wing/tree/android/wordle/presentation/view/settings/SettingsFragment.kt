package com.wing.tree.android.wordle.presentation.view.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.adapter.settings.SettingsListAdapter
import com.wing.tree.android.wordle.presentation.databinding.FragmentSettingsBinding
import com.wing.tree.android.wordle.presentation.model.settings.Settings
import com.wing.tree.android.wordle.presentation.util.Review
import com.wing.tree.android.wordle.presentation.util.shareApp
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    private val viewModel by viewModels<SettingsViewModel>()

    private val settingsListAdapter = SettingsListAdapter()

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        viewModel.settings.observe(viewLifecycleOwner) { settings ->

            val list = listOf(
                Settings.SwitchPreference(
                    id = 0L,
                    primaryText = getString(R.string.hard_mode),
                    isChecked = settings.isHardMode
                ).apply {
                    setOnCheckedChange { viewModel.updateHardMode(it) }
                },
                Settings.SwitchPreference(
                    id = 1L,
                    primaryText = getString(R.string.vibrate),
                    isChecked = settings.vibrates
                ).apply {
                    setOnCheckedChange { viewModel.updateVibrates(it) }
                },
                Settings.SwitchPreference(
                    id = 2L,
                    primaryText = getString(R.string.high_contrast_mode),
                    isChecked = settings.isHighContrastMode
                ).apply {
                    setOnCheckedChange { viewModel.updateHighContrastMode(it) }
                },
                Settings.Divider(id = 3L),
                Settings.Preference(
                    id = 4L,
                    primaryText = getString(R.string.write_review),
                    drawable = getDrawable(R.drawable.ic_round_rate_review_24),
                    isClickable = true
                ).apply {
                    setOnClick {
                        Review.launchReviewFlow(requireActivity())
                    }
                },
                Settings.Preference(
                    id = 5L,
                    primaryText = getString(R.string.share_the_app),
                    drawable = getDrawable(R.drawable.ic_round_share_24),
                    isClickable = true
                ).apply {
                    setOnClick {
                        shareApp(requireContext())
                    }
                },
                Settings.Preference(
                    id = 6L,
                    primaryText = getString(R.string.version),
                    drawable = getDrawable(R.drawable.ic_round_info_24),
                    isClickable = false
                ),
            )

            settingsListAdapter.submitList(list)
        }
    }

    override fun bind(viewBinding: FragmentSettingsBinding) {
        viewBinding.root.apply {
            adapter = settingsListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun getDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(requireContext(), id)
}