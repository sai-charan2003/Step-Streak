package com.charan.stepstreak.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.charan.stepstreak.data.local.entity.UserSetting
import com.charan.stepstreak.utils.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertUserSetting(userSetting: UserSetting)

    @Insert(onConflict = REPLACE)
    suspend fun insertUserSetting(userSetting: List<UserSetting>)

    @Query("SELECT settingValue FROM user_settings WHERE settingType = :settingType order by timestamp desc, id DESC limit 1")
    fun getTargetStep(settingType: String = Constants.STEPS_TARGET_SETTING): Flow<String?>

    @Query("""
    SELECT * FROM user_settings 
    WHERE settingType = :settingType 
    AND timestamp <= :givenTimestamp
    ORDER BY timestamp DESC, id DESC
    LIMIT 1
""")
     suspend fun getTargetStepInGivenTime(settingType: String = Constants.STEPS_TARGET_SETTING, givenTimestamp: Long): UserSetting?

     @Query("SELECT * FROM user_settings")
     suspend fun getAllSettings() : List<UserSetting>


}