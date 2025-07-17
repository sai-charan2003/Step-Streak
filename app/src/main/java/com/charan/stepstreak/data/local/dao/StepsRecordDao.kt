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
    SELECT a.*
    FROM steps_record AS a
    WHERE a.id = (
      SELECT b.id
      FROM steps_record AS b
      WHERE b.date = a.date
      ORDER BY b.steps DESC, b.id ASC
      LIMIT 1
    )
    ORDER BY a.date DESC;

""")

    fun getAllStepsRecords(): Flow<List<StepsRecordEntity>>

        @Query("""
    SELECT a.*
    FROM steps_record AS a
    WHERE a.id = (
      SELECT b.id
      FROM steps_record AS b
      WHERE b.date = a.date
        AND b.date BETWEEN :startDate AND :endDate
      ORDER BY b.steps DESC, b.id ASC
      LIMIT 1
    )
    AND a.date BETWEEN :startDate AND :endDate
    ORDER BY a.date DESC
    """)
    suspend fun getStepsRecordsByDateRange(startDate: String, endDate: String): List<StepsRecordEntity>

    @Query("SELECT * FROM steps_record")
    fun getAllStepRecords(): List<StepsRecordEntity>

    @Query("DELETE FROM steps_record")
    fun deleteAllStepsRecords()

    @Query("""
        SELECT *
        FROM steps_record
        WHERE date = :date
        ORDER BY steps DESC, id ASC
        LIMIT 1
        """)
    fun getStepsRecordByDate(date: String): StepsRecordEntity?


}