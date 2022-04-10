package com.wing.tree.android.wordle.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.wing.tree.android.wordle.data.datastore.statistics.Statistics
import com.wing.tree.android.wordle.data.datastore.statistics.StatisticsDataStore
import com.wing.tree.android.wordle.data.datastore.statistics.StatisticsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {
    @Singleton
    @Provides
    fun providesStatisticsDataStore(@ApplicationContext context: Context): DataStore<Statistics> {
        return DataStoreFactory.create(
            serializer = StatisticsSerializer,
            produceFile = { context.dataStoreFile(StatisticsDataStore.FILE_NAME) },
            corruptionHandler = null,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }
}