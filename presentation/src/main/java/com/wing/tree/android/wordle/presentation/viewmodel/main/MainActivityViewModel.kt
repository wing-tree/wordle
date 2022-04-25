package com.wing.tree.android.wordle.presentation.viewmodel.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.wing.tree.android.wordle.domain.usecase.billing.GetRemoveAdsPurchased
import com.wing.tree.android.wordle.domain.usecase.core.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    getRemoveAdsPurchasedUseCase: GetRemoveAdsPurchased,
    application: Application
) : AndroidViewModel(application) {
    val adsRemoved = getRemoveAdsPurchasedUseCase.invoke(Unit).map { result ->
        when(result) {
            is Result.Error -> false
            is Result.Success -> result.data
            Result.Loading -> false
        }
    }.asLiveData(viewModelScope.coroutineContext)

    val played = AtomicInteger(0)
}