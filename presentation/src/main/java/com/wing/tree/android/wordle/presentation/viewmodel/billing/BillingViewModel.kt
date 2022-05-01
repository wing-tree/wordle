package com.wing.tree.android.wordle.presentation.viewmodel.billing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.wing.tree.android.wordle.domain.usecase.billing.GetGoldUseCase
import com.wing.tree.android.wordle.domain.usecase.core.getOrDefault
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    getGoldUseCase: GetGoldUseCase,
    application: Application
) : AndroidViewModel(application) {
    val gold = getGoldUseCase(Unit).map { result ->
        result.getOrDefault(0)
    }.asLiveData(viewModelScope.coroutineContext)
}