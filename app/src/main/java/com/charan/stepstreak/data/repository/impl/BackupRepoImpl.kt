package com.charan.stepstreak.data.repository.impl

import android.content.Context
import android.net.Uri
import android.util.Log
import com.charan.stepstreak.data.model.BackupData
import com.charan.stepstreak.data.repository.BackupRepo
import com.charan.stepstreak.data.repository.DataStoreRepo
import com.charan.stepstreak.data.repository.StepsRecordRepo
import com.charan.stepstreak.data.repository.UsersSettingsRepo
import com.charan.stepstreak.utils.DateUtils
import com.charan.stepstreak.utils.ProcessState
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BackupRepoImpl @Inject constructor(
    private val context : Context,
    private val stepsRecordsRepo : StepsRecordRepo,
    private val userSettingsRepo : UsersSettingsRepo,
    private val dataStoreRepo: DataStoreRepo
): BackupRepo {
    override fun createBackup(uri: Uri?) : Flow<ProcessState<Boolean>> = flow{
        if(uri == null){
            emit(ProcessState.Error("Please Select a valid path to save the backup."))
            return@flow
        }
        val allData = BackupData(
            stepsData = stepsRecordsRepo.getAllStepsRecords(),
            settingsData = userSettingsRepo.getAllSettings(),
            backupTime = System.currentTimeMillis()
        )
        val json = Gson().toJson(allData)
        try {
            val outputStream = context.contentResolver.openOutputStream(uri)
            outputStream?.use { stream ->
                stream.write(json.toByteArray())
                stream.flush()
            }
            emit(ProcessState.Success(true))

        } catch (e: Exception) {
            emit(ProcessState.Error("Failed to create backup: ${e.message}"))
        }
    }

    override fun restoreBackup(uri : Uri?) : Flow<ProcessState<Boolean>>  = flow {
        if(uri == null){
            emit(ProcessState.Error("Please Select a valid backup file to restore."))
            return@flow
        }
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use{ stream->
                val data = stream.bufferedReader().readText()
                val backupData = Gson().fromJson(data, BackupData::class.java)
                stepsRecordsRepo.insertStepsRecord(backupData.stepsData)
                userSettingsRepo.insertSettings(backupData.settingsData)
                emit(ProcessState.Success(true))

            }
        } catch (e: Exception) {
            emit(ProcessState.Error("Failed to restore backup: ${e.message}"))
            return@flow
        }
    }

    override val fileName: String
        get() {
            val appName = context.applicationInfo.loadLabel(context.packageManager)
            return "$appName-${DateUtils.getCurrentDate()}-backup.json"
        }
}