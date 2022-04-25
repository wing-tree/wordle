package com.wing.tree.android.wordle.presentation.view.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wing.tree.android.wordle.presentation.databinding.FragmentResultBinding
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment
import com.wing.tree.android.wordle.presentation.viewmodel.result.ResultViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : BaseFragment<FragmentResultBinding>() {
    private val viewModel by viewModels<ResultViewModel>()

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentResultBinding {
        return FragmentResultBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        viewModel.statistics.observe(viewLifecycleOwner) {
            viewBinding.statisticsView.setStatistics(it)
        }
    }

    override fun bind(viewBinding: FragmentResultBinding) {
        with(viewBinding) {
            materialButtonNextWord.setOnClickListener {
                findNavController().navigate(ResultFragmentDirections.actionResultFragmentToPlayFragment())
            }
        }
    }
}