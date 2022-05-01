package com.wing.tree.android.wordle.presentation.delegate.ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.wing.tree.android.wordle.presentation.BuildConfig
import com.wing.tree.android.wordle.presentation.R

class InterstitialAdDelegateImpl: InterstitialAdDelegate {
    private var interstitialAd: InterstitialAd? = null

    override fun loadInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        val adUnitId = context.getString(
            if (BuildConfig.DEBUG) {
                R.string.sample_interstitial_ad_unit_id
            } else {
                R.string.interstitial_ad_unit_id
            }
        )

        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                interstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                this@InterstitialAdDelegateImpl.interstitialAd = interstitialAd
            }
        })
    }

    override fun showInterstitialAd(
        activity: Activity,
        onAdDismissedFullScreenContent: () -> Unit,
        onAdFailedToShowFullScreenContent: (adError: AdError) -> Unit,
    ) {
        val fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                onAdDismissedFullScreenContent()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                super.onAdFailedToShowFullScreenContent(adError)
                onAdFailedToShowFullScreenContent(adError)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                interstitialAd = null
            }
        }

        interstitialAd?.let {
            it.fullScreenContentCallback = fullScreenContentCallback
            it.show(activity)
        } ?: onAdDismissedFullScreenContent()
    }
}