package com.wing.tree.android.wordle.presentation.viewmodel.result

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.wing.tree.android.wordle.domain.usecase.core.Result
import com.wing.tree.android.wordle.domain.usecase.statistics.GetStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    getStatisticsUseCase: GetStatisticsUseCase,
    application: Application
) : AndroidViewModel(application) {
    val statistics = getStatisticsUseCase.invoke(Unit).asLiveData(viewModelScope.coroutineContext)
        .map { result ->
            when(result) {
                is Result.Error -> throw NullPointerException() // todo 기본값 던지도록 수정.
                is Result.Success -> result.data
            }
        }
}