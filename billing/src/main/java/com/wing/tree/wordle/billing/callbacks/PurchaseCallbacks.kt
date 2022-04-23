package com.wing.tree.wordle.billing.callbacks

import com.android.billingclient.api.Purchase

interface PurchaseCallbacks {
    fun onFailure(debugMessage: String, responseCode: Int) = Unit
    fun onPurchaseAcknowledged(purchase: Purchase) = Unit
    fun onPurchaseConsumed(purchase: Purchase) = Unit
}