package com.wing.tree.android.wordle.presentation.adapter.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wing.tree.android.wordle.presentation.view.onboarding.OnBoardingPageOneFragment

class OnBoardingFragmentStateAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = ITEM_COUNT

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> OnBoardingPageOneFragment()
            else -> throw IllegalArgumentException("$position")
        }
    }

    companion object {
        const val ITEM_COUNT = 1
    }
}