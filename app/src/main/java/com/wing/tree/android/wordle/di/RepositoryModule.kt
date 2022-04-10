package com.wing.tree.android.wordle.di

import com.wing.tree.android.wordle.data.repository.StatisticsRepositoryImpl
import com.wing.tree.android.wordle.data.repository.WordRepositoryImpl
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
    abstract fun bindsStatisticsRepository(repository: StatisticsRepositoryImpl): StatisticsRepository
}