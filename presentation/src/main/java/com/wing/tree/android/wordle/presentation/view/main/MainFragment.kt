package com.wing.tree.android.wordle.presentation.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.wing.tree.android.wordle.presentation.databinding.FragmentMainBinding
import com.wing.tree.android.wordle.presentation.delegate.ad.InterstitialAdDelegate
import com.wing.tree.android.wordle.presentation.delegate.ad.InterstitialAdDelegateImpl
import com.wing.tree.android.wordle.presentation.util.increment
import com.wing.tree.android.wordle.presentation.util.startActivity
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.view.onboarding.OnBoardingActivity
import com.wing.tree.android.wordle.presentation.viewmodel.main.MainActivityViewModel
import com.wing.tree.android.wordle.presentation.viewmodel.main.MainFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(), InterstitialAdDelegate by InterstitialAdDelegateImpl() {
    private val activityViewModel by activityViewModels<MainActivityViewModel>()
    private val viewModel by viewModels<MainFragmentViewModel>()

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        viewModel.statistics.observe(viewLifecycleOwner) {
            viewBinding.statisticsView.statistics = it
        }
    }

    override fun bind(viewBinding: FragmentMainBinding) {
        with(viewBinding) {
            buttonHowToPlay.setOnClickListener {
                startActivity<OnBoardingActivity>()
            }

            buttonPlay.setOnClickListener {
                val directions = MainFragmentDirections.actionMainFragmentToPlayFragment()

                activityViewModel.played.increment()
                navigate(directions)
            }
        }
    }
}