package com.wing.tree.android.wordle.presentation.view.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB: ViewBinding> : Fragment() {
    abstract fun inflate(inflater: LayoutInflater, container: ViewGroup?): VB
    abstract fun initData()
    abstract fun bind(viewBinding: VB)

    protected lateinit var viewBinding: VB

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        viewBinding = inflate(inflater, container)

        initData()

        return viewBinding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind(viewBinding)
    }
}