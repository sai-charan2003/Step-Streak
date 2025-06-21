package com.charan.stepstreak.data.model

import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import com.charan.stepstreak.data.local.entity.UserSetting
import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val stepsData : List<StepsRecordEntity>,
    val settingsData : List<UserSetting>,
    val backupTime: Long
)
