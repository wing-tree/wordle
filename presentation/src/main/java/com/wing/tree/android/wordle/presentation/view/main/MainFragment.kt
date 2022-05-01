package com.wing.tree.android.wordle.presentation.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.FragmentMainBinding
import com.wing.tree.android.wordle.presentation.util.increment
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.main.MainActivityViewModel
import com.wing.tree.android.wordle.presentation.viewmodel.main.MainFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {
    private val activityViewModel by activityViewModels<MainActivityViewModel>()
    private val viewModel by viewModels<MainFragmentViewModel>()

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        viewModel.gold.observe(viewLifecycleOwner) { gold ->
            viewBinding.textViewGold.text = "$gold"
        }

        viewModel.statistics.observe(viewLifecycleOwner) {
            viewBinding.statisticsView.setStatistics(it)
        }
    }

    override fun bind(viewBinding: FragmentMainBinding) {
        with(viewBinding) {
            textViewGold.setOnClickListener {
                val navigatorExtras = FragmentNavigatorExtras(
                    textViewGold to getString(R.string.text_view_gold)
                )

                val destinations = MainFragmentDirections.actionMainFragmentToBillingFragment()

                findNavController().navigate(destinations, navigatorExtras)
            }

            materialButtonPlay.setOnClickListener {
                val destinations = MainFragmentDirections.actionMainFragmentToPlayFragment()

                findNavController().navigate(destinations)
                activityViewModel.played.increment()
            }
        }
    }
}