package com.wing.tree.android.wordle.presentation.view.main

import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.ads.*
import com.wing.tree.android.wordle.presentation.BuildConfig
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.databinding.ActivityMainBinding
import com.wing.tree.android.wordle.presentation.view.base.BaseActivity
import com.wing.tree.android.wordle.presentation.view.play.PlayFragmentDirections
import com.wing.tree.android.wordle.presentation.view.result.ResultFragmentDirections
import com.wing.tree.android.wordle.presentation.viewmodel.main.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val viewModel by viewModels<MainActivityViewModel>()

    private val navController by lazy {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment

        navHostFragment?.navController
    }

    private val onDestinationChangedListener by lazy {
        NavController.OnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.billingFragment -> viewBinding.textViewCredits.isClickable = false
                else -> viewBinding.textViewCredits.isClickable = true
            }
        }
    }

    override fun onDestroy() {
        navController?.removeOnDestinationChangedListener(onDestinationChangedListener)
        super.onDestroy()
    }

    override fun inflate(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun initData() {
        MobileAds.initialize(this)

        navController?.addOnDestinationChangedListener(onDestinationChangedListener)

        viewModel.credits.observe(this) {
            viewBinding.textViewCredits.text = "$it"
        }

        viewModel.isAdsRemoved.observe(this) { isAdsRemoved ->
            val container = viewBinding.frameLayoutAdView

            if (isAdsRemoved) {
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

        viewModel.onCreditsClick.observe(this) {
            val directions = when(navController?.currentDestination?.id) {
                R.id.mainFragment -> MainFragmentDirections.actionMainFragmentToBillingFragment()
                R.id.playFragment -> PlayFragmentDirections.actionPlayFragmentToBillingFragment()
                R.id.resultFragment -> ResultFragmentDirections.actionResultFragmentToBillingFragment()
                else -> null
            }

            directions?.let { navigate(it) }
        }
    }

    override fun bind(viewBinding: ActivityMainBinding) {
        with(viewBinding) {
            textViewCredits.setOnClickListener {
                viewModel.callOnCreditsClick()
            }
        }
    }

    private fun navigate(directions: NavDirections) {
        try {
            navController?.navigate(directions)
        } catch (e: IllegalArgumentException) {
            Timber.e(e)
        }
    }
}