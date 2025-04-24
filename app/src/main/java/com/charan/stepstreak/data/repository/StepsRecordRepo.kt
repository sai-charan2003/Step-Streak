package com.charan.stepstreak.data.repository

import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import kotlinx.coroutines.flow.Flow

interface StepsRecordRepo {

    suspend fun insertStepsRecord(stepsRecordEntity: List<StepsRecordEntity>)

    suspend fun insertStepRecord(stepsRecordEntity: StepsRecordEntity)

    suspend fun getAllStepRecords() : Flow<List<StepsRecordEntity>>
}