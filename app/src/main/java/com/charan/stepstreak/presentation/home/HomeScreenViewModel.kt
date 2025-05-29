package com.charan.stepstreak.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.stepstreak.data.local.dao.StepsRecordDao
import com.charan.stepstreak.data.repository.DataStoreRepo
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.data.repository.StepsRecordRepo
import com.charan.stepstreak.data.repository.UsersSettingsRepo
import com.charan.stepstreak.data.repository.WidgetRepo
import com.charan.stepstreak.data.repository.impl.UserSettingsRepoImp
import com.charan.stepstreak.presentation.common.StepsData
import com.charan.stepstreak.utils.ProcessState
import com.charan.stepstreak.utils.getMotivationQuote
import com.charan.stepstreak.utils.getStreak
import com.charan.stepstreak.utils.toStepsData
import com.charan.stepstreak.utils.toWeekData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    private val stepsRecordRepo: StepsRecordRepo,
    private val widgetRepo: WidgetRepo,
    private val userSettingsRepo: UsersSettingsRepo
): ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<HomeViewEffect>()
    val effects = _effects.asSharedFlow()

    init {
        fetchSteps()
        observeSteps()
        getCurrentTargetSteps()
    }

    private fun fetchSteps() = viewModelScope.launch(Dispatchers.IO){
        healthConnectRepo.fetchAndSaveAllStepRecords().collectLatest { processState ->
            when(processState){
                is ProcessState.Error -> {
                    _state.update { it.copy(isSyncing = false) }
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

    private fun observeSteps() = viewModelScope.launch (Dispatchers.IO) {
        stepsRecordRepo.getAllStepRecords().collectLatest { status ->
            _state.update {
                it.copy(
                    streakCount = status.getStreak().toString(),
                    motivationText = status.getMotivationQuote(),
                    allStepsData = status.toStepsData(),

                )
            }
            getTodaysData()
            getCurrentWeekData()
            widgetRepo.updateWidget()
        }
    }

    private fun getTodaysData() = viewModelScope.launch (Dispatchers.IO){
        _state.update {
            it.copy(todayStepsData = stepsRecordRepo.getTodayStepData().toStepsData())
        }
    }

    private fun getCurrentWeekData() = viewModelScope.launch (Dispatchers.IO){
        _state.update {
            it.copy(currentWeekData = stepsRecordRepo.getWeeklyStepsRecords().toWeekData())
        }
    }

    private fun getCurrentTargetSteps() = viewModelScope.launch(Dispatchers.IO) {
        _state.update {
            it.copy(currentTargetSteps = userSettingsRepo.getStepsTargetInGivenTime(System.currentTimeMillis()))
        }
    }

    fun onEvent(event: HomeEvent){
        when(event){
            HomeEvent.OnRefresh -> {
                fetchSteps()
            }
        }

    }

}