package com.wing.tree.android.wordle.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.wing.tree.android.wordle.data.datastore.preferences.PreferencesDataStore
import com.wing.tree.android.wordle.data.datastore.playstate.PlayState
import com.wing.tree.android.wordle.data.datastore.playstate.PlayStateDataStore
import com.wing.tree.android.wordle.data.datastore.playstate.PlayStateSerializer
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
    fun providesPlayStateDataStore(@ApplicationContext context: Context): DataStore<PlayState> {
        return DataStoreFactory.create(
            serializer = PlayStateSerializer,
            produceFile = { context.dataStoreFile(PlayStateDataStore.FILE_NAME) },
            corruptionHandler = null,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

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

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(PreferencesDataStore.FILE_NAME) }
        )
    }
}