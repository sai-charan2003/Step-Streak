package com.charan.stepstreak.data.repository.impl

import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.readRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.charan.stepstreak.data.local.dao.StepsRecordDao
import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.utils.DateUtils
import com.charan.stepstreak.utils.ProcessState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class HealthConnectRepoImpl @Inject constructor(
    private val healthConnectClient: HealthConnectClient,
    private val stepsRecordDao: StepsRecordDao

) : HealthConnectRepo {
    companion object{
        val permissions = setOf(
            HealthPermission.getReadPermission(StepsRecord::class)
        )
    }
    override suspend fun getTotalSteps() : Flow<ProcessState<List<StepsRecordEntity>>> = flow {
        emit(ProcessState.Loading)
        try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest<StepsRecord>(
                    timeRangeFilter = TimeRangeFilter.before(LocalDateTime.now()),
                )
            )
            response.records.forEach { record->
                stepsRecordDao.insertStepsRecord(
                    StepsRecordEntity(
                        steps = record.count,
                        uuid = record.metadata.id,
                        date = DateUtils.convertUtcToLocalTime(utcTimeString = record.startTime, zoneOffsetString = record.startZoneOffset),

                    )
                )
            }

        } catch (e: Exception){
            e.printStackTrace()
            Log.d("TAG", "getTotalSteps: $e")
            emit(ProcessState.Error(e.message ?: "Something went wrong"))

        }

    }

    override suspend fun hasPermission(): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions().containsAll(permissions)
    }

    override fun requestPermission(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }

    override suspend fun fetchAndSaveAllStepRecords() {
        getTotalSteps().collect {

        }
    }
}