package com.charan.stepstreak.data.repository.impl

import com.charan.stepstreak.data.local.dao.StepsRecordDao
import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import com.charan.stepstreak.data.repository.StepsRecordRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StepsRecordRepoImp @Inject constructor(
    private val stepsRecordDao: StepsRecordDao
): StepsRecordRepo {
    override suspend fun insertStepsRecord(stepsRecordEntity: List<StepsRecordEntity>) {
        stepsRecordDao.insertStepsRecord(stepsRecordEntity)
    }

    override suspend fun insertStepRecord(stepsRecordEntity: StepsRecordEntity) {
        stepsRecordDao.insertStepsRecord(stepsRecordEntity)
    }

    override suspend fun getAllStepRecords(): Flow<List<StepsRecordEntity>> = flow{
        stepsRecordDao.getAllStepsRecords()
    }
}