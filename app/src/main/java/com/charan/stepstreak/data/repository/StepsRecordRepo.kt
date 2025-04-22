package com.charan.stepstreak.data.repository

import com.charan.stepstreak.data.local.entity.StepsRecordEntity

interface StepsRecordRepo {

    suspend fun insertStepsRecord(stepsRecordEntity: List<StepsRecordEntity>)

    suspend fun insertStepRecord(stepsRecordEntity: StepsRecordEntity)

    suspend fun getAllStepRecords() : List<StepsRecordEntity>
}