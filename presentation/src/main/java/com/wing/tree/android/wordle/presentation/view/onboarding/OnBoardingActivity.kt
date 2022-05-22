package com.wing.tree.android.wordle.presentation.view.onboarding

import androidx.viewpager2.widget.ViewPager2
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.adapter.onboarding.OnBoardingFragmentStateAdapter
import com.wing.tree.android.wordle.presentation.databinding.ActivityOnBoardingBinding
import com.wing.tree.android.wordle.presentation.extention.gone
import com.wing.tree.android.wordle.presentation.extention.visible
import com.wing.tree.android.wordle.presentation.view.base.BaseActivity

class OnBoardingActivity : BaseActivity<ActivityOnBoardingBinding>() {
    private val onBoardingFragmentStateAdapter = OnBoardingFragmentStateAdapter(this)
    private val onPageChangeCallback by lazy {
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                with(viewBinding) {
                    if (position == OnBoardingFragmentStateAdapter.ITEM_COUNT.dec()) {
                        materialButtonPrevious.gone()

                        materialButtonNext.text = getString(R.string.done)
                        materialButtonNext.setOnClickListener { finish() }
                    } else {
                        materialButtonPrevious.visible()

                        materialButtonNext.text = getString(R.string.next)
                        materialButtonNext.setOnClickListener {
                            val currentItem = viewBinding.viewPager2.currentItem

                            if (currentItem <= OnBoardingFragmentStateAdapter.ITEM_COUNT) {
                                viewBinding.viewPager2.setCurrentItem(currentItem.inc(), true)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun inflate(): ActivityOnBoardingBinding {
        return ActivityOnBoardingBinding.inflate(layoutInflater)
    }

    override fun initData() = Unit

    override fun bind(viewBinding: ActivityOnBoardingBinding) {
        with(viewBinding) {
            imageViewClose.setOnClickListener {
                finish()
            }

            viewPager2.apply {
                adapter = onBoardingFragmentStateAdapter
                registerOnPageChangeCallback(onPageChangeCallback)
            }

            if (onBoardingFragmentStateAdapter.itemCount > 1) {
                circleIndicator3.setViewPager(viewPager2)
            } else {
                circleIndicator3.gone()
            }

            materialButtonPrevious.setOnClickListener {
                val currentItem = viewPager2.currentItem

                if (currentItem > 0) {
                    viewPager2.setCurrentItem(currentItem.dec(), true)
                }
            }

            materialButtonNext.setOnClickListener {
                val currentItem = viewPager2.currentItem

                if (currentItem <= OnBoardingFragmentStateAdapter.ITEM_COUNT) {
                    viewPager2.setCurrentItem(currentItem.inc(), true)
                }
            }
        }
    }
}