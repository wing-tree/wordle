package com.wing.tree.android.wordle.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.wing.tree.android.wordle.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(private val dataStore: DataStore<Preferences>) : PreferencesRepository {
    private object Name {
        const val CREDITS = "CREDITS"
        const val IS_FIRST_TIME = "IS_FIRST_TIME"
        const val IS_REMOVE_ADS_PURCHASED = "IS_REMOVE_ADS_PURCHASED"
    }

    private object Key {
        val credits = intPreferencesKey(Name.CREDITS)
        val isFirstTime = booleanPreferencesKey(Name.IS_FIRST_TIME)
        val isRemoveAdsPurchased = booleanPreferencesKey(Name.IS_REMOVE_ADS_PURCHASED)
    }

    override fun getCredits(): Flow<Int> {
        return dataStore.data.map { it[Key.credits] ?: 0 }
    }

    override fun isFirstTime(): Flow<Boolean> {
        return dataStore.data.map { it[Key.isFirstTime] ?: true }
    }

    override fun isRemoveAdsPurchased(): Flow<Boolean> {
        return dataStore.data.map { it[Key.isRemoveAdsPurchased] ?: false }
    }

    override suspend fun consumeCredits(credits: Int) {
        dataStore.edit {
            with(it[Key.credits]) {
                it[Key.credits] = this?.minus(credits) ?: 0
            }
        }
    }

    override suspend fun purchaseCredits(credits: Int) {
        dataStore.edit {
            with(it[Key.credits]) {
                it[Key.credits] = this?.plus(credits) ?: credits
            }
        }
    }

    override suspend fun putFirstTime(isFirstTime: Boolean) {
        dataStore.edit { it[Key.isFirstTime] = isFirstTime }
    }

    override suspend fun putRemoveAdsPurchased(removeAdsPurchased: Boolean) {
        dataStore.edit { it[Key.isRemoveAdsPurchased] = removeAdsPurchased }
    }
}