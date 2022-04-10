package com.wing.tree.android.wordle.presentation.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.wing.tree.android.wordle.presentation.databinding.FragmentMainBinding
import com.wing.tree.android.wordle.presentation.view.base.BaseFragment

class MainFragment : BaseFragment<FragmentMainBinding>() {
    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun initData() {

    }

    override fun bind(viewBinding: FragmentMainBinding) {
        with(viewBinding) {
            materialButtonPlay.setOnClickListener {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToPlayFragment())
            }
        }
    }
}