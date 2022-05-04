package com.wing.tree.android.wordle.presentation.viewmodel.result

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.wing.tree.android.wordle.domain.model.Statistics
import com.wing.tree.android.wordle.domain.usecase.core.Result
import com.wing.tree.android.wordle.domain.usecase.playstate.ClearPlayStateUseCase
import com.wing.tree.android.wordle.domain.usecase.statistics.GetStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@DelicateCoroutinesApi
@HiltViewModel
class ResultViewModel @Inject constructor(
    private val clearPlayStateUseCase: ClearPlayStateUseCase,
    getStatisticsUseCase: GetStatisticsUseCase,
    application: Application
) : AndroidViewModel(application) {
    private val defaultDispatcher = Dispatchers.Default

    val statistics = getStatisticsUseCase.invoke(Unit).asLiveData(viewModelScope.coroutineContext)
        .map { result ->
            when(result) {
                is Result.Error -> Statistics.Default
                is Result.Success -> result.data
                Result.Loading -> Statistics.Default
            }
        }

    init {
        GlobalScope.launch(defaultDispatcher) {
            clearPlayStateUseCase(Unit)
        }
    }
}