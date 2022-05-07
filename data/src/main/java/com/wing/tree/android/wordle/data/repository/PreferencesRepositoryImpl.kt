package com.wing.tree.android.wordle.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.wing.tree.android.wordle.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(private val dataStore: DataStore<Preferences>) : PreferencesRepository {
    private object Default {
        const val CREDITS = 0
        const val IS_FIRST_TIME = true
        const val IS_REMOVE_ADS_PURCHASED = false
    }

    private object Name {
        private const val OBJECT_NAME = "Name"

        const val CREDITS = "$OBJECT_NAME.CREDITS"
        const val IS_FIRST_TIME = "$OBJECT_NAME.IS_FIRST_TIME"
        const val IS_REMOVE_ADS_PURCHASED = "$OBJECT_NAME.IS_REMOVE_ADS_PURCHASED"
    }

    private object Key {
        val Credits = intPreferencesKey(Name.CREDITS)
        val IsFirstTime = booleanPreferencesKey(Name.IS_FIRST_TIME)
        val IsRemoveAdsPurchased = booleanPreferencesKey(Name.IS_REMOVE_ADS_PURCHASED)
    }

    override fun getCredits(): Flow<Int> {
        return dataStore.data.map { it[Key.Credits] ?: Default.CREDITS }
    }

    override fun isFirstTime(): Flow<Boolean> {
        return dataStore.data.map { it[Key.IsFirstTime] ?: Default.IS_FIRST_TIME }
    }

    override fun isRemoveAdsPurchased(): Flow<Boolean> {
        return dataStore.data.map { it[Key.IsRemoveAdsPurchased] ?: Default.IS_REMOVE_ADS_PURCHASED }
    }

    override suspend fun consumeCredits(credits: Int): Boolean {
        return try {
            dataStore.edit {
                with(it[Key.Credits]) {
                    val value = this?.minus(credits) ?: -1

                    if (value < 0) {
                        throw IllegalArgumentException("$value")
                    } else {
                        it[Key.Credits] = value
                    }
                }
            }

            true
        } catch (exception: IllegalArgumentException) {
            Timber.e(exception)
            false
        }
    }

    override suspend fun purchaseCredits(credits: Int) {
        dataStore.edit {
            with(it[Key.Credits]) {
                it[Key.Credits] = this?.plus(credits) ?: credits
            }
        }
    }

    override suspend fun putFirstTime(isFirstTime: Boolean) {
        dataStore.edit { it[Key.IsFirstTime] = isFirstTime }
    }

    override suspend fun putRemoveAdsPurchased(isRemoveAdsPurchased: Boolean) {
        dataStore.edit { it[Key.IsRemoveAdsPurchased] = isRemoveAdsPurchased }
    }
}