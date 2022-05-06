package com.wing.tree.android.wordle.presentation.viewmodel.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.Purchase
import com.wing.tree.android.wordle.domain.usecase.billing.GetRemoveAdsPurchased
import com.wing.tree.android.wordle.domain.usecase.core.Result
import com.wing.tree.wordle.billing.callbacks.BillingClientStateCallbacks
import com.wing.tree.wordle.billing.callbacks.PurchaseCallbacks
import com.wing.tree.wordle.billing.delegate.BillingDelegate
import com.wing.tree.wordle.billing.delegate.BillingDelegateImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    getRemoveAdsPurchasedUseCase: GetRemoveAdsPurchased,
    application: Application
) : AndroidViewModel(application), BillingDelegate by BillingDelegateImpl {
    init {
        initBilling(application.applicationContext)
    }

    val adsRemoved = getRemoveAdsPurchasedUseCase.invoke(Unit).map { result ->
        when(result) {
            is Result.Error -> false
            is Result.Success -> result.data
            Result.Loading -> false
        }
    }.asLiveData(viewModelScope.coroutineContext)

    val played = AtomicInteger(0)

    private fun initBilling(context: Context) {
        build(context)

        startConnection(object : BillingClientStateCallbacks {
            override fun onBillingSetupFinished() {
                queryPurchasesAsync()
            }

            override fun onFailure(debugMessage: String, responseCode: Int) {
                Timber.e("debugMessage: $debugMessage, responseCode :$responseCode")
            }
        })

        registerPurchaseCallbacks(object : PurchaseCallbacks {
            override fun onPurchaseAcknowledged(purchase: Purchase) {

            }

            override fun onPurchaseConsumed(purchase: Purchase) {

            }

            override fun onPurchaseFailure(debugMessage: String, responseCode: Int) {

            }
        })
    }
}