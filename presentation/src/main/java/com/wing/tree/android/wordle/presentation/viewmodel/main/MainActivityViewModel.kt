package com.wing.tree.android.wordle.presentation.viewmodel.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.wing.tree.android.wordle.domain.model.settings.Settings
import com.wing.tree.android.wordle.domain.usecase.billing.GetCreditsUseCase
import com.wing.tree.android.wordle.domain.usecase.billing.IsRemoveAdsPurchasedUseCase
import com.wing.tree.android.wordle.domain.usecase.billing.PurchaseCreditsUseCase
import com.wing.tree.android.wordle.domain.usecase.billing.PutRemoveAdsPurchasedUseCase
import com.wing.tree.android.wordle.domain.usecase.core.getOrDefault
import com.wing.tree.android.wordle.domain.usecase.onboarding.IsFirstTimeUseCase
import com.wing.tree.android.wordle.domain.usecase.onboarding.PutFirstTimeUseCase
import com.wing.tree.android.wordle.domain.usecase.settings.GetSettingsUseCase
import com.wing.tree.android.wordle.domain.usecase.word.InsertWordsUseCase
import com.wing.tree.wordle.billing.callbacks.BillingClientStateCallbacks
import com.wing.tree.wordle.billing.callbacks.PurchaseCallbacks
import com.wing.tree.wordle.billing.delegate.BillingDelegate
import com.wing.tree.wordle.billing.delegate.BillingDelegateImpl
import com.wing.tree.wordle.billing.skus.Skus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val isFirstTimeUseCase: IsFirstTimeUseCase,
    private val purchaseCreditsUseCase: PurchaseCreditsUseCase,
    private val putFirstTimeUseCase: PutFirstTimeUseCase,
    private val putRemoveAdsPurchasedUseCase: PutRemoveAdsPurchasedUseCase,
    insertWordsUseCase: InsertWordsUseCase,
    getCreditsUseCase: GetCreditsUseCase,
    getSettingsUseCase: GetSettingsUseCase,
    isRemoveAdsPurchasedUseCaseUseCase: IsRemoveAdsPurchasedUseCase,
    application: Application
) : AndroidViewModel(application), BillingDelegate by BillingDelegateImpl {
    private val ioDispatcher = Dispatchers.IO

    init {
        initBilling(application.applicationContext)
        viewModelScope.launch(ioDispatcher) {
            insertWordsUseCase.invoke(listOf(
                "viper", "spike", "pearl", "grape", "badge",
                "humor", "pedal", "daddy", "tulip", "crape",
                "rocky", "paste", "venom", "color", "bingo",
                "flake", "prism", "nerdy", "denim", "spice",
                "cyber"
            ))
        }
    }

    private val _skuDetailsList = MutableLiveData<List<SkuDetails>>()
    val skuDetailsList: LiveData<List<SkuDetails>> get() = _skuDetailsList

    val credits = getCreditsUseCase().map { it.getOrDefault(0) }

    val isFirstTime: Flow<Boolean> get() = isFirstTimeUseCase().map { it.getOrDefault(false) }

    val isAdsRemoved = isRemoveAdsPurchasedUseCaseUseCase()
        .map { it.getOrDefault(false) }
        .asLiveData(viewModelScope.coroutineContext)

    val played = AtomicInteger(0)

    val settings = getSettingsUseCase()
        .map { it.getOrDefault(Settings.Default) }
        .shareIn(
            scope = viewModelScope,
            replay = 1,
            started = SharingStarted.WhileSubscribed()
        )

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
                val skus = purchase.skus

                GlobalScope.launch(ioDispatcher) {
                    if (Skus.REMOVE_ADS in skus) {
                        putRemoveAdsPurchasedUseCase(true)
                    }
                }
            }

            override fun onPurchaseConsumed(purchase: Purchase) {
                GlobalScope.launch(ioDispatcher) {
                    val skus = purchase.skus

                    val credits = when {
                        Skus.CREDITS_240 in skus -> Skus.credits[Skus.CREDITS_240]
                        Skus.CREDITS_720 in skus -> Skus.credits[Skus.CREDITS_720]
                        Skus.CREDITS_2000 in skus -> Skus.credits[Skus.CREDITS_2000]
                        Skus.CREDITS_6000 in skus -> Skus.credits[Skus.CREDITS_6000]
                        else -> null
                    }

                    credits?.let { purchaseCreditsUseCase(it) }
                }
            }

            override fun onPurchaseFailure(debugMessage: String, responseCode: Int) {

            }
        })
    }

    fun putNotFirstTime() {
        viewModelScope.launch(ioDispatcher) {
            putFirstTimeUseCase(false)
        }
    }
}