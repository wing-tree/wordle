package com.wing.tree.android.wordle.presentation.view.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB: ViewBinding>  : AppCompatActivity() {
    abstract fun inflate(): VB
    abstract fun initData()
    abstract fun bind(viewBinding: VB)

    protected val viewBinding: VB by lazy { inflate() }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        initData()
        bind(viewBinding)
    }
}