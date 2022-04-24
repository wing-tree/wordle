package com.wing.tree.android.wordle.presentation.view.billing

import android.view.LayoutInflater
import android.view.ViewGroup
import com.wing.tree.android.wordle.presentation.databinding.FragmentBillingBinding
import com.wing.tree.android.wordle.presentation.databinding.FragmentPlayBinding
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment

class BillingFragment : BaseFragment<FragmentBillingBinding>() {
    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentBillingBinding {
        return FragmentBillingBinding.inflate(inflater, container, false)
    }

    override fun initData() {

    }

    override fun bind(viewBinding: FragmentBillingBinding) {

    }
}