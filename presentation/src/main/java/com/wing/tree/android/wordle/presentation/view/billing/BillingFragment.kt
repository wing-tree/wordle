package com.wing.tree.android.wordle.presentation.view.billing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wing.tree.android.wordle.presentation.adapter.billing.SkuDetailsListAdapter
import com.wing.tree.android.wordle.presentation.databinding.FragmentBillingBinding
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.main.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BillingFragment : BaseFragment<FragmentBillingBinding>() {
    private val activityViewModel by activityViewModels<MainActivityViewModel>()
    private val skuDetailsListAdapter = SkuDetailsListAdapter { skuDetails ->
        if (isAdded) {
            activityViewModel.launchBillingFlow(requireActivity(), skuDetails)
        }
    }

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentBillingBinding {
        return FragmentBillingBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        activityViewModel.skuDetailsList.observe(viewLifecycleOwner) {
            skuDetailsListAdapter.submitList(it)
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