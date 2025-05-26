package com.charan.stepstreak.data.repository.impl

import com.charan.stepstreak.data.local.dao.UserSettingsDao
import com.charan.stepstreak.data.local.entity.UserSetting
import com.charan.stepstreak.data.repository.UsersSettingsRepo
import com.charan.stepstreak.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserSettingsRepoImp @Inject constructor(
    private val userSettingsDao: UserSettingsDao

) : UsersSettingsRepo {
    override suspend fun insertSetting(setting: String, value: String) {
        userSettingsDao.insertUserSetting(
            UserSetting(
                settingType = setting,
                settingValue = value,
                timestamp = DateUtils.startOfCurrentDayMillis
            )
        )
    }

    override suspend fun getStepsTarget(): Flow<Long> {
        return userSettingsDao.getTargetStep()
            .map { it?.toLong() ?: 10000L }
    }

    override suspend fun getStepsTargetInGivenTime(givenTime: Long): Long {
        return userSettingsDao.getTargetStepInGivenTime(givenTimestamp = givenTime)?.settingValue?.toLong() ?: 10000

    }

}