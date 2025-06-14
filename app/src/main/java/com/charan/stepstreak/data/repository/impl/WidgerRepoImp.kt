package com.charan.stepstreak.data.repository.impl

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.glance.appwidget.updateIf
import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import com.charan.stepstreak.data.repository.DataStoreRepo
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.data.repository.StepsRecordRepo
import com.charan.stepstreak.data.repository.WidgetRepo
import com.charan.stepstreak.presentation.common.StepsData
import com.charan.stepstreak.presentation.widget.DailyProgressWidget
import com.charan.stepstreak.presentation.widget.WeeklyStreakWidget
import com.charan.stepstreak.presentation.widget.WidgetState
import com.charan.stepstreak.utils.DateUtils
import com.charan.stepstreak.utils.ProcessState
import com.charan.stepstreak.utils.getMotivationQuote
import com.charan.stepstreak.utils.getStreak
import com.charan.stepstreak.utils.toStepsData
import com.charan.stepstreak.utils.toWidgetState
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetRepoImp @Inject constructor(
    private val healthConnectRepo: HealthConnectRepo,
    private val stepsRecordRepo: StepsRecordRepo,
    private val dataStoreRepo: DataStoreRepo,
    @ApplicationContext val context : Context,
) :  WidgetRepo{
    val coroutineScope= CoroutineScope(Dispatchers.IO)

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetRepoEntryPoint {
        fun widgetRepo(): WidgetRepo
    }
    init {
        coroutineScope.launch {
            healthConnectRepo.fetchAndSaveAllStepRecords()
        }
    }

    companion object {
        fun getInstance(application: Context): WidgetRepo {
            val widgetRepositoryEntryPoint: WidgetRepoEntryPoint = EntryPoints.get(
                application,
                WidgetRepoEntryPoint::class.java
            )
            return widgetRepositoryEntryPoint.widgetRepo()

        }
    }

    override fun getStepData(): Flow<WidgetState> = flow {
        val allData = stepsRecordRepo.getAllStepsRecords()
        val weekData = stepsRecordRepo.getWeeklyStepsRecords()

        emit(weekData.toWidgetState(allData,dataStoreRepo.startOfWeek.first()).copy(todayData = stepsRecordRepo.getTodayStepData().toStepsData()))
    }


    override suspend fun updateWidget() {
        WeeklyStreakWidget().updateAll(context)
        DailyProgressWidget().updateAll(context)
    }
}