package com.charan.stepstreak.data.repository.impl

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.glance.appwidget.updateIf
import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.data.repository.StepsRecordRepo
import com.charan.stepstreak.data.repository.WidgetRepo
import com.charan.stepstreak.presentation.widget.StepsData
import com.charan.stepstreak.presentation.widget.WeeklyStreakWidget
import com.charan.stepstreak.presentation.widget.WidgetState
import com.charan.stepstreak.utils.DateUtils
import com.charan.stepstreak.utils.ProcessState
import com.charan.stepstreak.utils.getMotivationQuote
import com.charan.stepstreak.utils.getStreak
import com.charan.stepstreak.utils.getTodaysStepsData
import com.charan.stepstreak.utils.toWidgetState
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetRepoImp @Inject constructor(
    private val healthConnectRepo: HealthConnectRepo,
    private val stepsRecordRepo: StepsRecordRepo,
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

    override fun getWeeklyStreak(): Flow<WidgetState> = flow{
        val weekList = DateUtils.weekList
        val data = stepsRecordRepo.getWeeklyStepsRecords().toWidgetState()
        val allData = stepsRecordRepo.getAllStepsRecords()
        val widgetContent = mutableListOf<StepsData>()
        weekList.forEach { day->
            val steps = data.stepsData.find { it.day == day }
            val stepsData = StepsData(
                steps = steps?.steps ?: 0L,
                targetSteps = steps?.targetSteps ?: 0L,
                targetCompleted = steps?.targetCompleted ?: false,
                date = steps?.date ?: "",
                day = day
            )
            widgetContent.add(stepsData)

        }
        emit(
            WidgetState(
                streak = allData.getStreak(),
                motiText = allData.getMotivationQuote(),
                stepsData = widgetContent
            )
        )

    }

    override suspend fun updateWidget() {
        WeeklyStreakWidget().updateAll(context)
    }

    override fun getDailyStreak(): Flow<WidgetState> =flow{

        val todayProgress = stepsRecordRepo.getAllStepsRecords().getTodaysStepsData()
        emit(
            WidgetState(
                streak = stepsRecordRepo.getAllStepsRecords().getStreak(),
                motiText = stepsRecordRepo.getAllStepsRecords().getMotivationQuote(),
                stepsData = listOf(
                    StepsData(
                        steps = todayProgress?.steps ?: 0L,
                        targetSteps = todayProgress?.targetSteps ?: 0L,
                        targetCompleted = todayProgress?.let {
                            it.steps > 0 && it.targetSteps > 0 && it.steps >= it.targetSteps
                        } ?: false,
                        date = todayProgress?.date ?: "",
                        day = DateUtils.getCurrentDate().toString()
                    )
                )
            )

        )
    }
}