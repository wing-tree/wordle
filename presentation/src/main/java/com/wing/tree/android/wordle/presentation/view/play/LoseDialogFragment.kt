package com.wing.tree.android.wordle.presentation.view.play

import android.view.LayoutInflater
import android.view.ViewGroup
import com.wing.tree.android.wordle.presentation.databinding.FragmentLoseDialogBinding
import com.wing.tree.android.wordle.presentation.view.base.BaseDialogFragment

class LoseDialogFragment : BaseDialogFragment<FragmentLoseDialogBinding>() {
    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoseDialogBinding {
        return FragmentLoseDialogBinding.inflate(inflater, container, false)
    }

    override fun initData() {

    }

    override fun bind(viewBinding: FragmentLoseDialogBinding) {

    }
}