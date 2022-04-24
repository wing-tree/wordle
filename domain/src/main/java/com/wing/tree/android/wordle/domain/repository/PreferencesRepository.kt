package com.wing.tree.android.wordle.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getFirstTime(): Flow<Boolean>
    fun getGold(): Flow<Int>
    fun getRemoveAdsPurchased(): Flow<Boolean>
    suspend fun consumeGold(gold: Int)
    suspend fun purchaseGold(gold: Int)
    suspend fun putIsFirstTime(isFirstTime: Boolean)
    suspend fun putRemoveAdsPurchased(removeAdsPurchased: Boolean)
}