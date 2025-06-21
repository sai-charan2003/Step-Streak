package com.charan.stepstreak.data.repository

import com.charan.stepstreak.data.local.entity.UserSetting
import kotlinx.coroutines.flow.Flow

interface UsersSettingsRepo {

    suspend fun insertSettings(settings: List<UserSetting>)

    suspend fun insertSetting(setting: String, value: String)

    suspend fun getStepsTarget() : Flow<Long>

    suspend fun getStepsTargetInGivenTime(givenTime : Long) : Long

    suspend fun getAllSettings() : List<UserSetting>
}