package com.wing.tree.android.wordle.di

import com.wing.tree.android.wordle.data.datasource.local.WordDataSource
import com.wing.tree.android.wordle.data.datasource.local.WordDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindsWordDataSource(dataSource: WordDataSourceImpl): WordDataSource
}