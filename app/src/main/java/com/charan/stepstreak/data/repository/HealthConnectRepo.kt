package com.charan.stepstreak.data.repository

import androidx.activity.result.contract.ActivityResultContract
import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import com.charan.stepstreak.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface HealthConnectRepo {

    suspend fun getTotalSteps() : Flow<ProcessState<List<StepsRecordEntity>>>

    suspend fun fetchAndSaveAllStepRecords() : Flow<ProcessState<Boolean>>

    suspend fun hasPermission() : Boolean

    fun requestPermission() : ActivityResultContract<Set<String>, Set<String>>

    fun getOriginProviders() : Flow<ProcessState<List<String>>>

}