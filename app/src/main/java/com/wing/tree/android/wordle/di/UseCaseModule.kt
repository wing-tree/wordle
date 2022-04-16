package com.wing.tree.android.wordle.di

import com.wing.tree.android.wordle.domain.repository.StatisticsRepository
import com.wing.tree.android.wordle.domain.repository.WordRepository
import com.wing.tree.android.wordle.domain.usecase.core.IOCoroutineDispatcher
import com.wing.tree.android.wordle.domain.usecase.statistics.GetStatisticsUseCase
import com.wing.tree.android.wordle.domain.usecase.word.ContainUseCase
import com.wing.tree.android.wordle.domain.usecase.word.GetCountUseCase
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
    ): ContainUseCase {
        return ContainUseCase(repository, coroutineDispatcher)
    }

    @Provides
    @ViewModelScoped
    fun providesGetCountUseCase(
        repository: WordRepository,
        @IOCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
    ): GetCountUseCase {
        return GetCountUseCase(repository, coroutineDispatcher)
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