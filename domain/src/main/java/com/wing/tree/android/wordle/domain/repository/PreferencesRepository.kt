package com.wing.tree.android.wordle.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun isFirstTime(): Flow<Boolean>
    fun getGold(): Flow<Int>
    fun getRemoveAdsPurchased(): Flow<Boolean>
    suspend fun consumeGold(gold: Int)
    suspend fun purchaseGold(gold: Int)
    suspend fun putFirstTime(isFirstTime: Boolean)
    suspend fun putRemoveAdsPurchased(removeAdsPurchased: Boolean)
}