package com.charan.stepstreak.data.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepo {
    val dataProviders : Flow<String>
    suspend fun setDataProviders(provider : String)
    suspend fun clearDataProviders()
    val isOnBoardingCompleted : Flow<Boolean>
    suspend fun setOnBoardingStatus(status : Boolean)
    val syncFrequency : Flow<Long>
    suspend fun setSyncFrequency(frequency : Long)



}