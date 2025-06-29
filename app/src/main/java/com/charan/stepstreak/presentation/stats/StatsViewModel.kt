package com.charan.stepstreak.presentation.stats

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.stepstreak.data.repository.StepsRecordRepo
import com.charan.stepstreak.presentation.common.components.StatEvents
import com.charan.stepstreak.utils.DateUtils
import com.charan.stepstreak.utils.toMonthData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val stepsRecordRepo : StepsRecordRepo
) : ViewModel() {
    private val _state = MutableStateFlow(StatsState())
    val state = _state.asStateFlow()

    init {
        fetchMonthlyData()
    }

    fun onEvent(event: StatEvents) = viewModelScope.launch {
        when (event) {
            is StatEvents.OnStatTypeSelected -> {
                _state.update { it.copy(selectedStatType = event.statType) }
            }
        }
    }


    private fun fetchMonthlyData() = viewModelScope.launch(Dispatchers.IO){
       _state.collectLatest {
           val month = it.monthlyData.periodLabel
           val monthStartDate = DateUtils.getMonthStartDate(month).toString()
           val monthEndDate = DateUtils.getMonthEndDate(month).toString()
           val stepsData = stepsRecordRepo.getStepsByDateRange(
               monthStartDate = monthStartDate,
               monthEndDate = monthEndDate
           )
           _state.update {
               it.copy(
                   monthlyData =  stepsData.toMonthData(),
               )
           }

       }
    }

}