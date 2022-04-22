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
        const val GOLD = "GOLD"
        const val IS_FIRST_TIME = "IS_FIRST_TIME"
        const val REMOVE_ADS_PURCHASED = "REMOVE_ADS_PURCHASED"
    }

    private object Key {
        val gold = intPreferencesKey(Name.GOLD)
        val isFirstTime = booleanPreferencesKey(Name.IS_FIRST_TIME)
        val removeAdsPurchased = booleanPreferencesKey(Name.REMOVE_ADS_PURCHASED)
    }

    override fun getFirstTime(): Flow<Boolean> {
        return dataStore.data.map { it[Key.isFirstTime] ?: true }
    }

    override fun getGold(): Flow<Int> {
        return dataStore.data.map { it[Key.gold] ?: 0 }
    }

    override fun getRemoveAdsPurchased(): Flow<Boolean> {
        return dataStore.data.map { it[Key.removeAdsPurchased] ?: false }
    }

    override suspend fun consumeGold(gold: Int) {
        dataStore.edit {
            with(it[Key.gold]) {
                it[Key.gold] = this?.minus(gold) ?: 0
            }
        }
    }

    override suspend fun earnGold(gold: Int) {
        dataStore.edit {
            with(it[Key.gold]) {
                it[Key.gold] = this?.plus(gold) ?: gold
            }
        }
    }

    override suspend fun putIsFirstTime(isFirstTime: Boolean) {
        dataStore.edit { it[Key.isFirstTime] = isFirstTime }
    }

    override suspend fun putRemoveAdsPurchased(removeAdsPurchased: Boolean) {
        dataStore.edit { it[Key.removeAdsPurchased] = removeAdsPurchased }
    }
}