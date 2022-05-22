package com.wing.tree.android.wordle.presentation.view.onboarding

import com.wing.tree.android.wordle.presentation.databinding.ActivityOnBoardingBinding
import com.wing.tree.android.wordle.presentation.view.base.BaseActivity

class OnBoardingActivity : BaseActivity<ActivityOnBoardingBinding>() {
    override fun inflate(): ActivityOnBoardingBinding {
        return ActivityOnBoardingBinding.inflate(layoutInflater)
    }

    override fun initData() = Unit

    override fun bind(viewBinding: ActivityOnBoardingBinding) {

    }
}