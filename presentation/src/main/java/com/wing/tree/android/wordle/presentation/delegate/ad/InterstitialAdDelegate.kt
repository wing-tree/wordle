package com.wing.tree.android.wordle.presentation.delegate.ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd

interface InterstitialAdDelegate {
    fun loadInterstitialAd(
        context: Context,
        onAdFailedToLoad: (LoadAdError?) -> Unit = { },
        onAdLoaded: (InterstitialAd) -> Unit = {  }
    )

    fun showInterstitialAd(
        activity: Activity,
        onAdDismissedFullScreenContent: () -> Unit = {  },
        onAdFailedToShowFullScreenContent: (adError: AdError?) -> Unit,
        onAdShowedFullScreenContent: () -> Unit = {  }
    )

    fun showInterstitialAd(
        activity: Activity,
        interstitialAd: InterstitialAd,
        onAdDismissedFullScreenContent: () -> Unit,
        onAdFailedToShowFullScreenContent: (adError: AdError?) -> Unit,
        onAdShowedFullScreenContent: () -> Unit,
    )
}