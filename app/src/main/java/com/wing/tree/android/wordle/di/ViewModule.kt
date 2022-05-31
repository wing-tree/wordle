package com.wing.tree.android.wordle.di

import android.content.Context
import com.wing.tree.android.wordle.presentation.util.Vibrator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ViewModule {
    @Singleton
    @Provides
    fun providesVibrator(@ApplicationContext context: Context): Vibrator {
        return Vibrator(context)
    }
}