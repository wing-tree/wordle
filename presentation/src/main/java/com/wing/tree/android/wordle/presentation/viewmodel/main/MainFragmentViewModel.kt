package com.wing.tree.android.wordle.presentation.viewmodel.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.wing.tree.android.wordle.domain.model.Statistics
import com.wing.tree.android.wordle.domain.usecase.billing.GetGoldUseCase
import com.wing.tree.android.wordle.domain.usecase.core.Result
import com.wing.tree.android.wordle.domain.usecase.core.getOrDefault
import com.wing.tree.android.wordle.domain.usecase.statistics.GetStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    getGoldUseCase: GetGoldUseCase,
    getStatisticsUseCase: GetStatisticsUseCase,
    application: Application
) : AndroidViewModel(application) {
    val gold = getGoldUseCase(Unit).map { result ->
        result.getOrDefault(0)
    }.asLiveData(viewModelScope.coroutineContext)

    val statistics = getStatisticsUseCase.invoke(Unit).asLiveData(viewModelScope.coroutineContext)
        .map { result ->
            when(result) {
                is Result.Error -> Statistics.Default
                is Result.Success -> result.data
                Result.Loading -> Statistics.Default
            }
        }
}