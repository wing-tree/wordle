package com.wing.tree.android.wordle.presentation.view.main

import android.os.Bundle
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.ActivityMainBinding
import com.wing.tree.android.wordle.presentation.view.base.BaseActivity
import com.wing.tree.android.wordle.presentation.view.play.PlayFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.frame_layout, PlayFragment.newInstance())
//            .commit()
    }

    override fun inflate(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun bind(viewBinding: ActivityMainBinding) {
        with(viewBinding) {

        }
    }
}