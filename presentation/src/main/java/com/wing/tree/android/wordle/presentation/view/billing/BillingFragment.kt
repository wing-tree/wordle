package com.wing.tree.android.wordle.presentation.view.billing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.wing.tree.android.wordle.presentation.databinding.FragmentBillingBinding
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.billing.BillingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BillingFragment : BaseFragment<FragmentBillingBinding>() {
    private val viewModel by viewModels<BillingViewModel>()

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentBillingBinding {
        return FragmentBillingBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        viewModel.gold.observe(viewLifecycleOwner) { gold ->
            viewBinding.textViewGold.text = "$gold"
        }
    }

    override fun bind(viewBinding: FragmentBillingBinding) {

    }
}