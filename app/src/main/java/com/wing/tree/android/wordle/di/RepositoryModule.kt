package com.wing.tree.android.wordle.di

import com.wing.tree.android.wordle.data.repository.*
import com.wing.tree.android.wordle.domain.repository.*
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
    abstract fun bindsItemCountRepository(repository: ItemCountRepositoryImpl): ItemCountRepository

    @Binds
    @Singleton
    abstract fun bindsPlayStateRepository(repository: PlayStateRepositoryImpl): PlayStateRepository

    @Binds
    @Singleton
    abstract fun bindsPreferencesRepository(repository: PreferencesRepositoryImpl): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindsStatisticsRepository(repository: StatisticsRepositoryImpl): StatisticsRepository

    @Binds
    @Singleton
    abstract fun bindsWordRepository(repository: WordRepositoryImpl): WordRepository
}