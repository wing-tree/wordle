package com.wing.tree.android.wordle.di

import com.wing.tree.android.wordle.domain.repository.PreferencesRepository
import com.wing.tree.android.wordle.domain.repository.StatisticsRepository
import com.wing.tree.android.wordle.domain.repository.WordRepository
import com.wing.tree.android.wordle.domain.usecase.billing.GetCreditsUseCase
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import com.wing.tree.android.wordle.domain.usecase.statistics.GetStatisticsUseCase
import com.wing.tree.android.wordle.domain.usecase.word.ContainsUseCase
import com.wing.tree.android.wordle.domain.usecase.word.GetWordUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher

@InstallIn(ViewModelComponent::class)
@Module
internal object UseCaseModule {
    @Provides
    @ViewModelScoped
    fun providesContainUseCase(
        repository: WordRepository,
        @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
    ): ContainsUseCase {
        return ContainsUseCase(repository, coroutineDispatcher)
    }

    @Provides
    @ViewModelScoped
    fun providesGetGoldUseCase(
        repository: PreferencesRepository,
        @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
    ): GetCreditsUseCase {
        return GetCreditsUseCase(repository, coroutineDispatcher)
    }

    @Provides
    @ViewModelScoped
    fun providesGetStatisticsUseCase(
        repository: StatisticsRepository,
        @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
    ): GetStatisticsUseCase {
        return GetStatisticsUseCase(repository, coroutineDispatcher)
    }

    @Provides
    @ViewModelScoped
    fun providesGetWordUseCase(
        repository: WordRepository,
        @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
    ): GetWordUseCase {
        return GetWordUseCase(repository, coroutineDispatcher)
    }
}