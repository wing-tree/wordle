package com.wing.tree.android.wordle.presentation.view.main

import com.wing.tree.android.wordle.presentation.databinding.ActivityMainBinding
import com.wing.tree.android.wordle.presentation.view.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun inflate(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun bind(viewBinding: ActivityMainBinding) = Unit
}