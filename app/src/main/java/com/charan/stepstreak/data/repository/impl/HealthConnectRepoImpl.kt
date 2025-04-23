package com.charan.stepstreak.data.repository.impl

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.utils.ProcessState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class HealthConnectRepoImpl @Inject constructor(
    private val healthConnectClient: HealthConnectClient

) : HealthConnectRepo {
    override suspend fun getTotalSteps() : Flow<ProcessState<List<StepsRecordEntity>>> = flow {
        emit(ProcessState.Loading)
        try {
            val response = healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(LocalDateTime.now().minusDays(1), LocalDateTime.now())
                )

            )
            StepsRecordEntity(
                steps = response[StepsRecord.COUNT_TOTAL],
                uuid = UUID.randomUUID().toString(),
            )

        } catch (e: Exception){
            e.printStackTrace()
            emit(ProcessState.Error(e.message ?: "Something went wrong"))

        }

    }
}