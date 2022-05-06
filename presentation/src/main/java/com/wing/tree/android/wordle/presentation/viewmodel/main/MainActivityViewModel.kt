package com.wing.tree.android.wordle.presentation.viewmodel.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.wing.tree.android.wordle.domain.usecase.billing.GetCreditsUseCase
import com.wing.tree.android.wordle.domain.usecase.billing.GetRemoveAdsPurchased
import com.wing.tree.android.wordle.domain.usecase.billing.PurchaseCreditsUseCase
import com.wing.tree.android.wordle.domain.usecase.core.Result
import com.wing.tree.android.wordle.domain.usecase.core.getOrDefault
import com.wing.tree.wordle.billing.callbacks.BillingClientStateCallbacks
import com.wing.tree.wordle.billing.callbacks.PurchaseCallbacks
import com.wing.tree.wordle.billing.delegate.BillingDelegate
import com.wing.tree.wordle.billing.delegate.BillingDelegateImpl
import com.wing.tree.wordle.billing.skus.Skus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val purchaseCreditsUseCase: PurchaseCreditsUseCase,
    getCreditsUseCase: GetCreditsUseCase,
    getRemoveAdsPurchasedUseCase: GetRemoveAdsPurchased,
    application: Application
) : AndroidViewModel(application), BillingDelegate by BillingDelegateImpl {
    init {
        initBilling(application.applicationContext)
    }

    private val ioDispatcher = Dispatchers.IO

    private val _skuDetailsList = MutableLiveData<List<SkuDetails>>()
    val skuDetailsList: LiveData<List<SkuDetails>> get() = _skuDetailsList

    val adsRemoved = getRemoveAdsPurchasedUseCase()
        .map { it.getOrDefault(false) }
        .asLiveData(viewModelScope.coroutineContext)

    val credits = getCreditsUseCase()
        .map { it.getOrDefault(0) }
        .asLiveData(viewModelScope.coroutineContext)

    val played = AtomicInteger(0)

    @OptIn(DelicateCoroutinesApi::class)
    private fun initBilling(context: Context) {
        build(context)

        startConnection(object : BillingClientStateCallbacks {
            override fun onBillingSetupFinished() {
                queryPurchasesAsync()
                querySkuDetails { _skuDetailsList.postValue(it) }
            }

            override fun onFailure(debugMessage: String, responseCode: Int) {
                Timber.e("debugMessage: $debugMessage, responseCode :$responseCode")
            }
        })

        registerPurchaseCallbacks(object : PurchaseCallbacks {
            override fun onPurchaseAcknowledged(purchase: Purchase) {

            }

            override fun onPurchaseConsumed(purchase: Purchase) {
                GlobalScope.launch(ioDispatcher) {
                    val skus = purchase.skus

                    when {
                        Skus.CREDITS_240 in skus -> {
                            purchaseCreditsUseCase(240)
                        }
                        Skus.CREDITS_720 in skus -> {
                            purchaseCreditsUseCase(720)
                        }
                    }
                }
            }

            override fun onPurchaseFailure(debugMessage: String, responseCode: Int) {

            }
        })
    }
}