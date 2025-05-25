package com.charan.stepstreak.data.repository.impl

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.charan.stepstreak.R
import com.charan.stepstreak.data.local.dao.StepsRecordDao
import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import com.charan.stepstreak.data.model.DataProviders
import com.charan.stepstreak.data.repository.DataStoreRepo
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.data.repository.UsersSettingsRepo
import com.charan.stepstreak.data.repository.WidgetRepo
import com.charan.stepstreak.utils.DateUtils
import com.charan.stepstreak.utils.ProcessState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

val permissions = setOf(
    HealthPermission.getReadPermission(StepsRecord::class),
    HealthPermission.PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND,
    HealthPermission.PERMISSION_READ_HEALTH_DATA_HISTORY
)

class HealthConnectRepoImpl @Inject constructor(
    private val healthConnectClient: HealthConnectClient,
    private val stepsRecordDao: StepsRecordDao,
    private val context : Context,
    private val dataStore : DataStoreRepo,
    private val usersSettingsRepo: UsersSettingsRepo

) : HealthConnectRepo {
    override suspend fun getTotalSteps() : Flow<ProcessState<List<StepsRecordEntity>>> = flow {
        emit(ProcessState.Loading)
        try {
            val records = mutableListOf<StepsRecordEntity>()
            val provider = dataStore.dataProviders.first()
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest<StepsRecord>(
                    timeRangeFilter = TimeRangeFilter.before(LocalDateTime.now()),
                    dataOriginFilter = setOf(DataOrigin(provider)),
                    ascendingOrder = false
                )
            )
            response.records.forEach { record->
                val stepRecord = StepsRecordEntity(
                    steps = record.count,
                    stepTarget = 10000,
                    uuid = UUID.randomUUID().toString(),
                    date = DateUtils.convertUtcToLocalTime(record.startTime, record.startZoneOffset)

                )
                records.add(stepRecord)

            }
            emit(ProcessState.Success(records))


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

    override suspend fun fetchAndSaveAllStepRecords(): Flow<ProcessState<Boolean>> = flow {
        emit(ProcessState.Loading)
        try {
            var pageToken: String? = null
            do {
                val response = healthConnectClient.readRecords(
                    ReadRecordsRequest<StepsRecord>(
                        timeRangeFilter = TimeRangeFilter.before(LocalDateTime.now()),
                        ascendingOrder = true,
                        pageToken = pageToken,
                    )
                )

                response.records.forEach {
                    stepsRecordDao.insertStepsRecord(
                        StepsRecordEntity(
                            steps = it.count,
                            stepTarget = usersSettingsRepo.getStepsTargetInGivenTime(
                                DateUtils.convertToTimeMillis(it.startTime, it.startZoneOffset)
                            ),
                            uuid = it.metadata.id,
                            date = DateUtils.convertUtcToLocalTime(it.startTime, it.startZoneOffset)
                        )
                    )
                }

                pageToken = response.pageToken
            } while (pageToken.isNullOrEmpty().not())
            emit(ProcessState.Success(true))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ProcessState.Error(e.message ?: "Something went wrong"))
        }
    }


    override fun getOriginProviders(): Flow<ProcessState<List<DataProviders>>> = flow{
        emit(ProcessState.Loading)
        val dataProviders : MutableList<DataProviders> = mutableListOf()
        val selectedDataProviderPackage = dataStore.dataProviders.first()
        try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest<StepsRecord>(
                    timeRangeFilter = TimeRangeFilter.before(LocalDateTime.now()),
                    ascendingOrder = false
                )
            )
            val distinctProviders = response.records.map { it.metadata.dataOrigin.packageName }.distinct()
            distinctProviders.forEach {
                val dataProvider = DataProviders(
                    packageName = it,
                    name = getApplicationName(it),
                    icon = getApplicationIcon(it),
                    isConnected = selectedDataProviderPackage == it
                )
                dataProviders.add(dataProvider)

            }
            emit(ProcessState.Success(dataProviders))

        }catch (e: Exception){
            e.printStackTrace()
            emit(ProcessState.Error(e.message ?: "Something went wrong"))

        }

    }

    private fun getApplicationIcon(packageName: String): Drawable {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            e.printStackTrace()
            ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)
                ?: throw RuntimeException("Default icon not found")
        }
    }


    private fun getApplicationName(packageName: String): String {
        return try {
            val applicationInfo = context.packageManager.getApplicationInfo(packageName, 0)
            context.packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            packageName
        }
    }

    override suspend fun getCurrentWeekSteps(): Flow<ProcessState<List<StepsRecordEntity>>> = flow{
        emit(ProcessState.Loading)
        try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest<StepsRecord>(
                    timeRangeFilter = TimeRangeFilter.between(DateUtils.getWeekStartDateInISO(),
                        DateUtils.getWeekEndDateInISO()),
                    ascendingOrder = false
                )
            )
            response.records.forEach {
                stepsRecordDao.insertStepsRecord(
                    StepsRecordEntity(
                        steps = it.count,
                        stepTarget = usersSettingsRepo.getStepsTargetInGivenTime(DateUtils.convertToTimeMillis(it.startTime,it.startZoneOffset)),
                        uuid = UUID.randomUUID().toString(),
                        date = DateUtils.convertUtcToLocalTime(it.startTime, it.startZoneOffset)
                    )
                )
            }


        } catch (e: Exception){
            e.printStackTrace()
            emit(ProcessState.Error(e.message ?: "Something went wrong"))

        }
    }
}