package com.charan.stepstreak.presentation.stats

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.stepstreak.data.model.StatType
import com.charan.stepstreak.data.repository.DataStoreRepo
import com.charan.stepstreak.data.repository.StepsRecordRepo
import com.charan.stepstreak.presentation.common.PeriodStepsData
import com.charan.stepstreak.presentation.common.components.StatEvents
import com.charan.stepstreak.utils.DateUtils
import com.charan.stepstreak.utils.toGraphData
import com.charan.stepstreak.utils.toMonthData
import com.charan.stepstreak.utils.toWeekData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val stepsRecordRepo : StepsRecordRepo,
    private val dataRepo : DataStoreRepo
) : ViewModel() {
    private val _state = MutableStateFlow(StatsState())
    val state = _state.asStateFlow()

    init {
        initValue()
    }

    private fun initValue() = viewModelScope.launch {
        _state.update {
            it.copy(
                currentMonth = DateUtils.getCurrentMonthWithYear(),
                currentWeek = DateUtils.getCurrentWeekRange(dataRepo.startOfWeek.first())
            )

        }
        fetchMonthlyData()
        updateFormatedWeekDate()
    }

    fun onEvent(event: StatEvents) = viewModelScope.launch {
        when (event) {
            is StatEvents.OnStatTypeSelected -> {
                onStatTypeSelected(statType = event.statType)
            }

            StatEvents.DecrementPeriod -> {
                decrementPeriod()

            }
            StatEvents.IncrementPeriod -> {
                incrementPeriod()

            }
        }
    }

    private fun decrementPeriod() = viewModelScope.launch {
        val selectedStatType = state.value.selectedStatType
        val currentMonth = state.value.currentMonth

            if(selectedStatType == StatType.MONTHLY){
                val currentMonth = currentMonth
                val previousMonth = DateUtils.getPreviousMonth(currentMonth)
                _state.update {
                    it.copy(
                        currentMonth = previousMonth,
                        stepData = PeriodStepsData(),
                        graphData = emptyList()
                    )
                }
                fetchMonthlyData()
            } else if(selectedStatType == StatType.WEEKLY){
                val currentWeek = state.value.currentWeek
                val nextWeek = DateUtils.getPreviousWeekRange(weekFirstDate = currentWeek.first, userSetWeekStart = dataRepo.startOfWeek.first())
                _state.update {
                    it.copy(
                        currentWeek = nextWeek,
                        stepData = PeriodStepsData(),
                        graphData = emptyList()
                    )
                }
                fetchWeeklyData()
            }


    }

    private fun incrementPeriod() = viewModelScope.launch {
        val selectedStatType = state.value.selectedStatType
        val currentMonth = state.value.currentMonth
            if(selectedStatType == StatType.MONTHLY){
                val nextMonth = DateUtils.getNextMonth(currentMonth)
                _state.update {
                    it.copy(
                        currentMonth = nextMonth,
                        stepData = PeriodStepsData(),
                        graphData = emptyList()
                    )
                }
                fetchMonthlyData()
            } else if(selectedStatType == StatType.WEEKLY){
                val currentWeek = state.value.currentWeek
                val nextWeek = DateUtils.getNextWeekRange(weekLastDate = currentWeek.second, userSetWeekStart = dataRepo.startOfWeek.first())
                _state.update {
                    it.copy(
                        currentWeek = nextWeek,
                        stepData = PeriodStepsData(),
                        graphData = emptyList()
                    )
                }
                fetchWeeklyData()


            }

    }

    private fun onStatTypeSelected(statType: StatType) = viewModelScope.launch {
        _state.update { it.copy(selectedStatType = statType) }
        when (statType) {
            StatType.WEEKLY -> fetchWeeklyData()
            StatType.MONTHLY -> fetchMonthlyData()
        }
    }


    private fun fetchMonthlyData() = viewModelScope.launch(Dispatchers.IO){
           val month = state.value.currentMonth
           val monthStartDate = DateUtils.getMonthStartDate(month).toString()
           val monthEndDate = DateUtils.getMonthEndDate(month).toString()
           val stepsData = stepsRecordRepo.getStepsByDateRange(
               monthStartDate = monthStartDate,
               monthEndDate = monthEndDate
           )
           _state.update {
               it.copy(
                   stepData =  stepsData.toMonthData(month),
               )
           }
           updateMonthlyGraphData()

    }

    private fun fetchWeeklyData() = viewModelScope.launch(Dispatchers.IO) {
        val week = state.value.currentWeek
        val weekData = stepsRecordRepo.getStepsByDateRange(
            monthStartDate = week.first,
            monthEndDate = week.second
        )
        _state.update {
            it.copy(
                stepData = weekData.toWeekData(dataRepo.startOfWeek.first())
            )
        }
        updateWeeklyGraphData()

    }

    private fun updateWeeklyGraphData() = viewModelScope.launch(Dispatchers.IO) {
        val weekData = state.value.stepData
        _state.update {
            it.copy(
                graphData = weekData.toGraphData()
            )


        }

    }

    private fun updateFormatedWeekDate() = viewModelScope.launch(Dispatchers.IO) {
        _state.collectLatest {
            val week = it.currentWeek
            val formattedWeek = "${DateUtils.getFormattedDate(week.first)} - ${DateUtils.getFormattedDate(week.second)}"
            _state.update {
                it.copy(
                    formatedWeek = formattedWeek
                )
            }
        }

    }

    private fun updateMonthlyGraphData() = viewModelScope.launch(Dispatchers.IO) {
        val graphData = state.value.stepData.toGraphData()
        _state.update {
            it.copy(
                graphData = graphData
            )
        }
    }

}