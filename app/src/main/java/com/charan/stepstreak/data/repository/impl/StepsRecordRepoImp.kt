package com.charan.stepstreak.data.repository.impl

import com.charan.stepstreak.data.local.dao.StepsRecordDao
import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import com.charan.stepstreak.data.repository.StepsRecordRepo
import com.charan.stepstreak.data.repository.UsersSettingsRepo
import com.charan.stepstreak.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StepsRecordRepoImp @Inject constructor(
    private val stepsRecordDao: StepsRecordDao,
    private val usersSettingsRepo: UsersSettingsRepo
): StepsRecordRepo {
    override suspend fun insertStepsRecord(stepsRecordEntity: List<StepsRecordEntity>) {
        stepsRecordDao.insertStepsRecord(stepsRecordEntity)
    }

    override suspend fun insertStepRecord(stepsRecordEntity: StepsRecordEntity) {
        stepsRecordDao.insertStepsRecord(stepsRecordEntity)
    }

    override suspend fun getAllStepRecords(): Flow<List<StepsRecordEntity>> {
        return stepsRecordDao.getAllStepsRecords()
    }


    override suspend fun getWeeklyStepsRecords(): List<StepsRecordEntity> {
        return stepsRecordDao.getStepsRecordsByDateRange(DateUtils.getCurrentWeekStartDate().toString(), DateUtils.getCurrentWeekEndDate().toString())
    }

    override suspend fun getAllStepsRecords(): List<StepsRecordEntity> {
        return stepsRecordDao.getAllStepRecords()
    }

    override suspend fun deleteAllStepRecords() {
        stepsRecordDao.deleteAllStepsRecords()
    }

    override suspend fun getTodayStepData(): StepsRecordEntity {
        return stepsRecordDao.getStepsRecordByDate(DateUtils.getCurrentDate().toString()) ?: StepsRecordEntity(
            steps = 0,
            stepTarget = usersSettingsRepo.getStepsTargetInGivenTime(System.currentTimeMillis()),
            uuid = "",
            date = DateUtils.getCurrentDate().toString()
        )
    }
}