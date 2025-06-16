package com.charan.stepstreak.presentation.home

import com.charan.stepstreak.presentation.common.StepsData
import com.charan.stepstreak.presentation.common.WeeklyData

data class HomeState(
    val allStepsData : List<StepsData> = emptyList(),
    val isSyncing : Boolean = true,
    val isPermissionGranted : Boolean = true,
    val streakCount : String = "0",
    val motivationText : String = "",
    val todayStepsData : StepsData = StepsData(),
    val currentWeekData : WeeklyData = WeeklyData(),
    val currentTargetSteps : Long = 0L,
)



