package com.wing.tree.android.wordle.di

import com.wing.tree.android.wordle.data.repository.PlayStateRepositoryImpl
import com.wing.tree.android.wordle.data.repository.PreferencesRepositoryImpl
import com.wing.tree.android.wordle.data.repository.StatisticsRepositoryImpl
import com.wing.tree.android.wordle.data.repository.WordRepositoryImpl
import com.wing.tree.android.wordle.domain.repository.PlayStateRepository
import com.wing.tree.android.wordle.domain.repository.PreferencesRepository
import com.wing.tree.android.wordle.domain.repository.StatisticsRepository
import com.wing.tree.android.wordle.domain.repository.WordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsWordRepository(repository: WordRepositoryImpl): WordRepository

    @Binds
    @Singleton
    abstract fun bindsPlayStateRepository(repository: PlayStateRepositoryImpl): PlayStateRepository

    @Binds
    @Singleton
    abstract fun bindsPreferencesRepository(repository: PreferencesRepositoryImpl): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindsStatisticsRepository(repository: StatisticsRepositoryImpl): StatisticsRepository
}