package com.charan.stepstreak.data.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.charan.stepstreak.data.repository.DataStoreRepo
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.data.repository.StepsRecordRepo
import com.charan.stepstreak.presentation.widget.DailyProgressWidget
import com.charan.stepstreak.presentation.widget.WeeklyStreakWidget
import com.charan.stepstreak.presentation.widget.WeeklyStreakWidgetReceiver
import com.charan.stepstreak.utils.NotificationHelper
import com.charan.stepstreak.utils.ProcessState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltWorker
class StepsUpdateWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Assisted val healthConnectRepo: HealthConnectRepo,
    @Assisted val dataStoreRepo: DataStoreRepo,
    @Assisted val notificationHelper: NotificationHelper,
    @Assisted val stepRepo : StepsRecordRepo
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "StepsUpdateWorker"
        fun setup(context: Context) {
            val constraints = Constraints.Builder()
                .build()

            val request = PeriodicWorkRequestBuilder<StepsUpdateWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setInitialDelay(Duration.ofSeconds(15))
                .setConstraints(constraints)

                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                request
            )
        }
    }

    override suspend fun doWork(): Result {
        try {
            healthConnectRepo.fetchAndSaveAllStepRecords().collectLatest {
                when(it){
                    is ProcessState.Error -> {
                    }
                    ProcessState.Loading -> {}
                    is ProcessState.Success<*> -> {
                        getStepsPercentage()
                        WeeklyStreakWidget().updateAll(appContext)
                        DailyProgressWidget().updateAll(appContext)
                    }
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Result.success()

    }

    private suspend fun getStepsPercentage() {
        val todaySteps = stepRepo.getTodayStepData()
        val steps = todaySteps.steps?.toLong() ?: 0L
        val target = todaySteps.stepTarget?.toLong() ?: 0L

        if (target == 0L) return

        val rawPercentage = (steps * 100 / target).toInt()
        val milestone = when {
            rawPercentage >= 100 -> 100
            rawPercentage >= 75 -> 75
            rawPercentage >= 50 -> 50
            rawPercentage >= 25 -> 25
            else -> 0
        }

        if (milestone > 0 && dataStoreRepo.shouldShowMilestone(milestone)) {
            if(notificationHelper.isPermissionGranted()) {
                notificationHelper.showStepMilestoneNotification(milestone)
                dataStoreRepo.markMilestoneAsShown(milestone)
            }
        }
    }

}