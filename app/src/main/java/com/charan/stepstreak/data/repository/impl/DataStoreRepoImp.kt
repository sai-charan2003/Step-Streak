package com.charan.stepstreak.data.repository.impl

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.charan.stepstreak.data.repository.DataStoreRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreRepoImp (
    private val context : Context
): DataStoreRepo {

    companion object {
        private val Context.dataStore by preferencesDataStore("settings")
        val DATA_PROVIDERS = stringPreferencesKey("data_providers")
        val ON_BOARDING_STATUS = booleanPreferencesKey("on_boarding_status")
        val TARGET_STEPS = stringPreferencesKey("target_steps")
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

    override val targetSteps: Flow<String>
        get() = context.dataStore.data.map {
            it[TARGET_STEPS] ?: "10000"
        }

    override suspend fun setTargetSteps(steps: String) {
        context.dataStore.edit {
            it[TARGET_STEPS] = steps
        }
    }
}