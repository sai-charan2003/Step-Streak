package com.charan.stepstreak.data.repository.impl

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.charan.stepstreak.data.repository.DataStoreRepo
import com.charan.stepstreak.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class DataStoreRepoImp (
    private val context : Context
): DataStoreRepo {

    companion object {
        private val Context.dataStore by preferencesDataStore("settings")
        val DATA_PROVIDERS = stringPreferencesKey("data_providers")
        val ON_BOARDING_STATUS = booleanPreferencesKey("on_boarding_status")
        val SYNC_FREQUENCY = longPreferencesKey("sync_frequency")
        val MILESTONE_25_SHOWN = booleanPreferencesKey("milestone_25_shown")
        val MILESTONE_50_SHOWN = booleanPreferencesKey("milestone_50_shown")
        val MILESTONE_75_SHOWN = booleanPreferencesKey("milestone_75_shown")
        val MILESTONE_100_SHOWN = booleanPreferencesKey("milestone_100_shown")
        val LAST_MILESTONE_DATE = stringPreferencesKey("last_milestone_date")
    }

    override val dataProviders: Flow<String>
        get() = context.dataStore.data.map { pref->
            pref[DATA_PROVIDERS] ?: ""
        }

    override suspend fun setDataProviders(provider: String) {
        context.dataStore.edit { pref ->
            pref[DATA_PROVIDERS] = provider

        }
    }

    override suspend fun clearDataProviders() {
        context.dataStore.edit { pref ->
            pref.remove(DATA_PROVIDERS)

        }
    }

    override val isOnBoardingCompleted: Flow<Boolean>
        get() = context.dataStore.data.map { pref->
            pref[ON_BOARDING_STATUS] == true
        }

    override suspend fun setOnBoardingStatus(status: Boolean) {
        context.dataStore.edit {pref->
            pref[ON_BOARDING_STATUS] = status
        }
    }

    override val syncFrequency: Flow<Long>
        get() = context.dataStore.data.map {
            it[SYNC_FREQUENCY] ?: 15L
        }

    override suspend fun setSyncFrequency(frequency: Long) {
        context.dataStore.edit {
            it[SYNC_FREQUENCY] = frequency
        }

    }

    override suspend fun shouldShowMilestone(percentage: Int): Boolean {
        resetMilestonesIfNewDay()
        val key = when (percentage) {
            25 -> MILESTONE_25_SHOWN
            50 -> MILESTONE_50_SHOWN
            75 -> MILESTONE_75_SHOWN
            100 -> MILESTONE_100_SHOWN
            else -> return false
        }

        val alreadyShown = context.dataStore.data.map { it[key] ?: false }.first()
        return !alreadyShown
    }

    override suspend fun markMilestoneAsShown(percentage: Int) {
        val key = when (percentage) {
            25 -> MILESTONE_25_SHOWN
            50 -> MILESTONE_50_SHOWN
            75 -> MILESTONE_75_SHOWN
            100 -> MILESTONE_100_SHOWN
            else -> return
        }

        context.dataStore.edit {
            it[key] = true
        }
        context.dataStore.edit {
            it[LAST_MILESTONE_DATE] = DateUtils.getCurrentDate().toString()
        }
    }

    private suspend fun resetMilestonesIfNewDay() {
        val today = DateUtils.getCurrentDate().toString()
        val lastDate = context.dataStore.data.map { it[LAST_MILESTONE_DATE] }.firstOrNull()

        if (today != lastDate) {
            context.dataStore.edit {
                it[LAST_MILESTONE_DATE] = today
                it[MILESTONE_25_SHOWN] = false
                it[MILESTONE_50_SHOWN] = false
                it[MILESTONE_75_SHOWN] = false
                it[MILESTONE_100_SHOWN] = false
            }
        }
    }
}