package com.charan.stepstreak.data.repository

import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import kotlinx.coroutines.flow.Flow

interface StepsRecordRepo {

    suspend fun insertStepsRecord(stepsRecordEntity: List<StepsRecordEntity>)

    suspend fun insertStepRecord(stepsRecordEntity: StepsRecordEntity)

    suspend fun getAllStepRecords() : Flow<List<StepsRecordEntity>>

    suspend fun getWeeklyStepsRecords() : List<StepsRecordEntity>

    suspend fun getAllStepsRecords() : List<StepsRecordEntity>

    suspend fun deleteAllStepRecords()

    suspend fun getTodayStepData() : StepsRecordEntity

    suspend fun getStepsByDateRange(monthStartDate: String, monthEndDate : String): List<StepsRecordEntity>
}