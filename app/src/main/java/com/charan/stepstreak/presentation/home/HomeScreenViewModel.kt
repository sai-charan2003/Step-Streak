package com.charan.stepstreak.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.stepstreak.data.local.dao.StepsRecordDao
import com.charan.stepstreak.data.repository.HealthConnectRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val healthConnectRepo: HealthConnectRepo,
    private val stepsRecordDao: StepsRecordDao

): ViewModel() {
    init {
        fetchSteps()
    }

    val stepsRecord = stepsRecordDao.getAllStepsRecords()

    private fun fetchSteps() = viewModelScope.launch(Dispatchers.IO){
        healthConnectRepo.fetchAndSaveAllStepRecords()
    }

}