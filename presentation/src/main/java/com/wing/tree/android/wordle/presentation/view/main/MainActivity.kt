package com.wing.tree.android.wordle.presentation.view.main

import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.ads.*
import com.wing.tree.android.wordle.presentation.BuildConfig
import com.wing.tree.android.wordle.presentation.R
import com.wing.tree.android.wordle.presentation.constant.Duration
import com.wing.tree.android.wordle.presentation.databinding.ActivityMainBinding
import com.wing.tree.android.wordle.presentation.eventbus.Event
import com.wing.tree.android.wordle.presentation.eventbus.EventBus
import com.wing.tree.android.wordle.presentation.extention.fadeIn
import com.wing.tree.android.wordle.presentation.extention.fadeOut
import com.wing.tree.android.wordle.presentation.util.captureScreen
import com.wing.tree.android.wordle.presentation.util.deleteCapturedScreen
import com.wing.tree.android.wordle.presentation.util.sendPngImage
import com.wing.tree.android.wordle.presentation.util.startActivity
import com.wing.tree.android.wordle.presentation.view.base.BaseActivity
import com.wing.tree.android.wordle.presentation.view.billing.BillingFragmentDirections
import com.wing.tree.android.wordle.presentation.view.onboarding.OnBoardingActivity
import com.wing.tree.android.wordle.presentation.view.play.PlayFragmentDirections
import com.wing.tree.android.wordle.presentation.view.result.ResultFragmentDirections
import com.wing.tree.android.wordle.presentation.view.settings.SettingsFragmentDirections
import com.wing.tree.android.wordle.presentation.viewmodel.main.MainActivityViewModel
import com.yy.mobile.rollingtextview.CharOrder
import com.yy.mobile.rollingtextview.strategy.Strategy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
            with(viewBinding) {
                when(destination.id) {
                    R.id.billingFragment -> {
                        fadeIn(imageViewArrowBack)
                        fadeOut(buttonAsk, imageViewSettings)
                        frameLayoutCredits.isClickable = false
                    }
                    R.id.mainFragment -> {
                        fadeIn(imageViewSettings)
                        fadeOut(buttonAsk, imageViewArrowBack)
                        frameLayoutCredits.isClickable = true
                    }
                    R.id.playFragment -> {
                        fadeIn(buttonAsk, imageViewArrowBack, imageViewSettings)
                        frameLayoutCredits.isClickable = true
                    }
                    R.id.settingsFragment -> {
                        fadeIn(imageViewArrowBack)
                        fadeOut(buttonAsk, imageViewSettings)
                        frameLayoutCredits.isClickable = true
                    }
                    else -> {
                        fadeIn(imageViewArrowBack, imageViewSettings)
                        fadeOut(buttonAsk)
                        frameLayoutCredits.isClickable = true
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        deleteCapturedScreen()
        navController?.removeOnDestinationChangedListener(onDestinationChangedListener)
        super.onDestroy()
    }

    override fun inflate(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun initData() {
        MobileAds.initialize(this)

        navController?.addOnDestinationChangedListener(onDestinationChangedListener)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.credits.collectLatest {
                    viewBinding.rollingTextViewCredits.setText("$it")
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                EventBus.getInstance().subscribeEvent<Event.Exception.NotEnoughCredits> {
                    val directions = PlayFragmentDirections.actionPlayFragmentToBillingFragment()

                    navigate(directions)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isFirstTime.collectLatest {
                if (it) {
                    viewModel.putNotFirstTime()
                    startActivity<OnBoardingActivity>()
                } else {
                    cancel()
                }
            }
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
    }

    override fun bind(viewBinding: ActivityMainBinding) {
        with(viewBinding) {
            imageViewArrowBack.setOnClickListener {
                onBackPressed()
            }

            imageViewSettings.setOnClickListener {
                lifecycleScope.launch {
                    delay(Duration.SHORT)

                    val directions = when(navController?.currentDestination?.id) {
                        R.id.billingFragment -> BillingFragmentDirections.actionBillingFragmentToSettingsFragment()
                        R.id.mainFragment -> MainFragmentDirections.actionMainFragmentToSettingsFragment()
                        R.id.playFragment -> PlayFragmentDirections.actionPlayFragmentToSettingsFragment()
                        R.id.resultFragment -> ResultFragmentDirections.actionResultFragmentToSettingsFragment()
                        else -> null
                    }

                    directions?.let { navigate(it) }
                }
            }

            buttonAsk.setOnClickListener {
                captureScreen({
                    sendPngImage(it)
                }) {
                    Timber.e(it)
                }
            }

            frameLayoutCredits.setOnClickListener {
                lifecycleScope.launch {
                    delay(Duration.SHORT)

                    val directions = when(navController?.currentDestination?.id) {
                        R.id.mainFragment -> MainFragmentDirections.actionMainFragmentToBillingFragment()
                        R.id.playFragment -> PlayFragmentDirections.actionPlayFragmentToBillingFragment()
                        R.id.resultFragment -> ResultFragmentDirections.actionResultFragmentToBillingFragment()
                        R.id.settingsFragment -> SettingsFragmentDirections.actionSettingsFragmentToBillingFragment()
                        else -> null
                    }

                    directions?.let { navigate(it) }
                }
            }

            with(rollingTextViewCredits) {
                addCharOrder(CharOrder.Number)

                animationDuration = Duration.LONG
                animationInterpolator = AccelerateDecelerateInterpolator()
                charStrategy = Strategy.NormalAnimation()
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