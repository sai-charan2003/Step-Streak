package com.charan.stepstreak.data.repository

import androidx.activity.result.contract.ActivityResultContract
import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import com.charan.stepstreak.data.model.DataProviders
import com.charan.stepstreak.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface HealthConnectRepo {

    fun hasHealthConnectClient() : Boolean

    fun installHealthConnect()
    suspend fun fetchAndSaveAllStepRecords() : Flow<ProcessState<Boolean>>

    suspend fun hasPermission() : Boolean

    fun getOriginProviders() : Flow<ProcessState<List<DataProviders>>>

    fun openSettingsPermission()


}