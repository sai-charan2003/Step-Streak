package com.charan.stepstreak.data.repository

import android.net.Uri
import com.charan.stepstreak.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface BackupRepo {
    companion object{
        const val FILE_TYPE = "application/json"
    }

    fun createBackup(uri : Uri?) : Flow<ProcessState<Boolean>>

    fun restoreBackup(uri : Uri?) : Flow<ProcessState<Boolean>>
    val fileName : String
}