package com.wing.tree.android.wordle.presentation.viewmodel.billing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.wing.tree.android.wordle.domain.usecase.billing.GetCreditsUseCase
import com.wing.tree.android.wordle.domain.usecase.core.getOrDefault
import com.wing.tree.wordle.billing.delegate.BillingDelegate
import com.wing.tree.wordle.billing.delegate.BillingDelegateImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    getCreditsUseCase: GetCreditsUseCase,
    application: Application
) : AndroidViewModel(application), BillingDelegate by BillingDelegateImpl {
    val gold = getCreditsUseCase(Unit).map { result ->
        result.getOrDefault(0)
    }.asLiveData(viewModelScope.coroutineContext)
}