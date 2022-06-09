package com.wing.tree.android.wordle.presentation.view.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.adapter.settings.SettingsListAdapter
import com.wing.tree.android.wordle.presentation.databinding.FragmentSettingsBinding
import com.wing.tree.android.wordle.presentation.model.settings.Settings
import com.wing.tree.android.wordle.presentation.util.Review
import com.wing.tree.android.wordle.presentation.util.shareApp
import com.wing.tree.android.wordle.presentation.util.versionName
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.main.MainActivityViewModel
import com.wing.tree.android.wordle.presentation.viewmodel.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    private val activityViewModel by activityViewModels<MainActivityViewModel>()
    private val viewModel by viewModels<SettingsViewModel>()
    private val settingsListAdapter = SettingsListAdapter()

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                activityViewModel.settings.collectLatest { settings ->
                    val list = listOf(
                        Settings.SwitchPreference(
                            id = 0L,
                            isChecked = settings.isHardMode,
                            primaryText = getString(R.string.hard_mode),
                            secondaryText = getString(R.string.hard_mode_description)
                        ).apply {
                            setOnCheckedChange { viewModel.updateHardMode(it) }
                        },
                        Settings.SwitchPreference(
                            id = 1L,
                            isChecked = settings.vibrates,
                            primaryText = getString(R.string.vibrate),
                        ).apply {
                            setOnCheckedChange { viewModel.updateVibrates(it) }
                        },
                        Settings.SwitchPreference(
                            id = 2L,
                            isChecked = settings.isHighContrastMode,
                            primaryText = getString(R.string.high_contrast_mode),
                            secondaryText = getString(R.string.high_contrast_mode_description)
                        ).apply {
                            setOnCheckedChange { viewModel.updateHighContrastMode(it) }
                        },
                        Settings.Divider(id = 3L),
                        Settings.Preference(
                            id = 4L,
                            drawable = getDrawable(R.drawable.ic_round_rate_review_24),
                            isClickable = true,
                            primaryText = getString(R.string.write_review),
                        ).apply {
                            setOnClick {
                                Review.launchReviewFlow(requireActivity())
                            }
                        },
                        Settings.Preference(
                            id = 5L,
                            drawable = getDrawable(R.drawable.ic_round_share_24),
                            isClickable = true,
                            primaryText = getString(R.string.share_the_app),
                        ).apply {
                            setOnClick {
                                shareApp(requireContext())
                            }
                        },
                        Settings.Preference(
                            id = 6L,
                            drawable = getDrawable(R.drawable.ic_round_info_24),
                            isClickable = false,
                            primaryText = getString(R.string.version),
                            secondaryText = requireContext().versionName
                        ),
                    )

                    settingsListAdapter.submitList(list)
                }
            }
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