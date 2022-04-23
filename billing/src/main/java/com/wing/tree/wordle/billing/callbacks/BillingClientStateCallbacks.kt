package com.wing.tree.wordle.billing.callbacks

interface BillingClientStateCallbacks {
    fun onBillingSetupFinished() = Unit
    fun onFailure(debugMessage: String, responseCode: Int) = Unit
}