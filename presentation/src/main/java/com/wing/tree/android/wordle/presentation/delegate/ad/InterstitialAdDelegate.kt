package com.wing.tree.android.wordle.presentation.delegate.ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError

interface InterstitialAdDelegate {
    fun loadInterstitialAd(context: Context)
    fun showInterstitialAd(
        activity: Activity,
        onAdFailedToShowFullScreenContent: (adError: AdError) -> Unit,
        onAdShowedFullScreenContent: () -> Unit
    )
}