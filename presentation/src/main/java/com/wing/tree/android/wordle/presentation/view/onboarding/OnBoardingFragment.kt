package com.wing.tree.android.wordle.presentation.view.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import com.wing.tree.android.wordle.presentation.databinding.FragmentOnBoardingBinding
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment

class OnBoardingFragment : BaseFragment<FragmentOnBoardingBinding>() {
    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnBoardingBinding {
        return FragmentOnBoardingBinding.inflate(inflater, container, false)
    }

    override fun initData() = Unit

    override fun bind(viewBinding: FragmentOnBoardingBinding) {

    }
}