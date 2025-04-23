package com.charan.stepstreak.data.repository

import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import com.charan.stepstreak.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface HealthConnectRepo {

    suspend fun getTotalSteps() : Flow<ProcessState<List<StepsRecordEntity>>>

}