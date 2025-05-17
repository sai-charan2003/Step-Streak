package com.charan.stepstreak.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StepsRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStepsRecord(stepsRecordEntity: StepsRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStepsRecord(stepsRecordEntity: List<StepsRecordEntity>)

    @Query("""
        SELECT * FROM steps_record 
        WHERE steps IN (
            SELECT MAX(steps) 
            FROM steps_record 
            GROUP BY date
        )
        ORDER BY date DESC
    """)
    fun getAllStepsRecords(): Flow<List<StepsRecordEntity>>

    @Query("SELECT * FROM steps_record WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getStepsRecordsByDateRange(startDate: String, endDate: String): List<StepsRecordEntity>

    @Query("SELECT * FROM steps_record")
    fun getAllStepRecords(): List<StepsRecordEntity>

    @Query("DELETE FROM steps_record")
    fun deleteAllStepsRecords()


}