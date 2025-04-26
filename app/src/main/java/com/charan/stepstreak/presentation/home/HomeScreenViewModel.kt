package com.charan.stepstreak.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.stepstreak.data.local.dao.StepsRecordDao
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.utils.ProcessState
import com.charan.stepstreak.utils.toStepsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val healthConnectRepo: HealthConnectRepo,
    private val stepsRecordDao: StepsRecordDao

): ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<HomeViewEffect>()
    val effects = _effects.asSharedFlow()

    val stepsRecord = stepsRecordDao.getAllStepsRecords()

    init {
        fetchSteps()
        observeSteps()
    }

    private fun fetchSteps() = viewModelScope.launch(Dispatchers.IO){
        healthConnectRepo.fetchAndSaveAllStepRecords().collectLatest { processState ->
            when(processState){
                is ProcessState.Error -> {
                    _effects.emit(HomeViewEffect.ShowError(processState.message))

                }
                ProcessState.Loading -> {
                    _state.update { it.copy(isSyncing = true) }
                }
                is ProcessState.Success -> {
                    _state.update { it.copy(isSyncing = false) }
                }
            }

        }
    }

    private fun observeSteps() = viewModelScope.launch (Dispatchers.IO){
        stepsRecordDao.getAllStepsRecords().collectLatest { status ->
            _state.update { it.copy(stepsData = status.toStepsData()) }

        }
    }

}