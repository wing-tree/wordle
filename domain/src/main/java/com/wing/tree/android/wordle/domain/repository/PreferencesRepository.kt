package com.wing.tree.android.wordle.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getCredits(): Flow<Int>
    fun isFirstTime(): Flow<Boolean>
    fun isRemoveAdsPurchased(): Flow<Boolean>
    suspend fun consumeCredits(credits: Int): Boolean
    suspend fun purchaseCredits(credits: Int)
    suspend fun putFirstTime(isFirstTime: Boolean)
    suspend fun putRemoveAdsPurchased(removeAdsPurchased: Boolean)
}