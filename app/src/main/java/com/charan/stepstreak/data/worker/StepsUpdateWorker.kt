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
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.presentation.widget.WeeklyStreakWidget
import com.charan.stepstreak.presentation.widget.WeeklyStreakWidgetReceiver
import com.charan.stepstreak.utils.ProcessState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collectLatest
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltWorker
class StepsUpdateWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Assisted val healthConnectRepo: HealthConnectRepo
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
            println("Starting workerManager")
            healthConnectRepo.fetchAndSaveAllStepRecords().collectLatest {
                if (it == ProcessState.Success) {
                    WeeklyStreakWidget().updateAll(appContext)

                }
            }
        } catch (e: Exception) {
            Log.d("TAG", "doWork: $e")

        }
        return Result.success()

    }
}