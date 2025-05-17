package com.charan.stepstreak.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSetting(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val settingType: String,
    val settingValue: String,
    val timestamp: Long,
)
