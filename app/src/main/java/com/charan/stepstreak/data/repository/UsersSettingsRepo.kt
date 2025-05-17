package com.charan.stepstreak.data.repository

import kotlinx.coroutines.flow.Flow

interface UsersSettingsRepo {

    suspend fun insertSetting(setting: String, value: String)

    suspend fun getStepsTarget() : Flow<Long>

    suspend fun getStepsTargetInGivenTime(givenTime : Long) : Long
}