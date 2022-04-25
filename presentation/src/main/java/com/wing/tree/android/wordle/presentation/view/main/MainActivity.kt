package com.wing.tree.android.wordle.presentation.view.main

import androidx.activity.viewModels
import com.google.android.gms.ads.*
import com.wing.tree.android.wordle.presentation.BuildConfig
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.ActivityMainBinding
import com.wing.tree.android.wordle.presentation.view.base.BaseActivity
import com.wing.tree.android.wordle.presentation.viewmodel.main.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val viewModel by viewModels<MainActivityViewModel>()

    override fun inflate(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun initData() {
        MobileAds.initialize(this)

        viewModel.adsRemoved.observe(this) { adsRemoved ->
            val container = viewBinding.frameLayoutAdView

            if (adsRemoved) {
                container.removeAllViews()
            } else {
                val adView = AdView(this).apply {
                    adSize = AdSize.BANNER
                    adUnitId = if (BuildConfig.DEBUG) {
                        getString(R.string.sample_banner_ad_unit_id)
                    } else {
                        getString(R.string.banner_ad_unit_id)
                    }

                    adListener = object : AdListener() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            super.onAdFailedToLoad(adError)
                            container.removeAllViews()
                        }
                    }

                    loadAd(AdRequest.Builder().build())
                }

                container.addView(adView)
            }
        }
    }

    override fun bind(viewBinding: ActivityMainBinding) = Unit
}