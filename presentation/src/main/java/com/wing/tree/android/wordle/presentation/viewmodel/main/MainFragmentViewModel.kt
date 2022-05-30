package com.wing.tree.android.wordle.presentation.viewmodel.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.wing.tree.android.wordle.domain.model.staticstics.Statistics
import com.wing.tree.android.wordle.domain.usecase.core.getOrDefault
import com.wing.tree.android.wordle.domain.usecase.statistics.GetStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    getStatisticsUseCase: GetStatisticsUseCase,
    application: Application
) : AndroidViewModel(application) {
   val statistics = getStatisticsUseCase()
       .map { it.getOrDefault(Statistics.Default) }
       .asLiveData(viewModelScope.coroutineContext)
}