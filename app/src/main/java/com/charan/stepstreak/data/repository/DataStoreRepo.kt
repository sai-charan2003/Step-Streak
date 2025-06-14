package com.charan.stepstreak.data.repository

import com.charan.stepstreak.data.model.StartOfWeekEnums
import com.charan.stepstreak.data.model.ThemeEnum
import kotlinx.coroutines.flow.Flow

interface DataStoreRepo {
    val dataProviders : Flow<String>
    suspend fun setDataProviders(provider : String)
    suspend fun clearDataProviders()
    val isOnBoardingCompleted : Flow<Boolean>
    suspend fun setOnBoardingStatus(status : Boolean)
    val syncFrequency : Flow<Long>
    suspend fun setSyncFrequency(frequency : Long)
    suspend fun shouldShowMilestone(milestone : Int) : Boolean
    suspend fun markMilestoneAsShown(milestone : Int)
    val isDynamicColor : Flow<Boolean>
    suspend fun changeDynamicColorStatus(status : Boolean)
    val theme : Flow<ThemeEnum>
    suspend fun setTheme(theme: ThemeEnum)
    val startOfWeek : Flow<StartOfWeekEnums>
    suspend fun setStartOfWeek(startOfWeek: StartOfWeekEnums)



}