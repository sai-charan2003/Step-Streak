package com.charan.stepstreak.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charan.stepstreak.data.local.entity.StepsRecordEntity

@Dao
interface StepsRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStepsRecord(stepsRecordEntity: StepsRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStepsRecord(stepsRecordEntity: List<StepsRecordEntity>)

    @Query("SELECT * FROM steps_record")
    suspend fun getAllStepsRecords(): List<StepsRecordEntity>

}