package com.wing.tree.android.wordle.presentation.view.billing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wing.tree.android.wordle.presentation.adapter.billing.SkuDetailsListAdapter
import com.wing.tree.android.wordle.presentation.databinding.FragmentBillingBinding
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.billing.BillingViewModel
import com.wing.tree.android.wordle.presentation.viewmodel.main.MainActivityViewModel
import com.wing.tree.wordle.billing.delegate.BillingDelegate
import com.wing.tree.wordle.billing.delegate.BillingDelegateImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BillingFragment : BaseFragment<FragmentBillingBinding>() {
    private val activityViewModel by activityViewModels<MainActivityViewModel>()
    private val viewModel by viewModels<BillingViewModel>()
    private val skuDetailsListAdapter = SkuDetailsListAdapter { skuDetails ->
        if (isAdded) {
            activityViewModel.launchBillingFlow(requireActivity(), skuDetails)
        }
    }

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentBillingBinding {
        return FragmentBillingBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        activityViewModel.querySkuDetails {
            println("ssssss:$it")
            skuDetailsListAdapter.submitList(it)
        }

        viewModel.gold.observe(viewLifecycleOwner) { gold ->
            viewBinding.textViewGold.text = "$gold"
        }
    }

    override fun bind(viewBinding: FragmentBillingBinding) {
        with(viewBinding) {
            recyclerView.apply {
                adapter = skuDetailsListAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }
}