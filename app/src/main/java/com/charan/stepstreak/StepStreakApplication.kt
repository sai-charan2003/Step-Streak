package com.charan.stepstreak

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.charan.stepstreak.data.repository.DataStoreRepo
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.data.repository.StepsRecordRepo
import com.charan.stepstreak.data.worker.StepsUpdateWorker
import com.charan.stepstreak.utils.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class StepStreakApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: WorkerFactorClass
    override fun onCreate() {
        super.onCreate()
    }
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

class WorkerFactorClass @Inject constructor(
    private val healthConnectRepo: HealthConnectRepo,
    private val dataStoreRepo: DataStoreRepo,
    private val notificationHelper: NotificationHelper,
    private val stepRecord : StepsRecordRepo) : WorkerFactory(){
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker?  {
        return when(workerClassName){
            StepsUpdateWorker::class.java.name ->
                StepsUpdateWorker(appContext,workerParameters,healthConnectRepo,dataStoreRepo,notificationHelper,stepRecord)
            else ->
                return null

        }

    }

}