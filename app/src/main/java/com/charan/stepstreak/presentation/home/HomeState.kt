package com.charan.stepstreak.presentation.home

import com.charan.stepstreak.presentation.common.StepsData

data class HomeState(
    val allStepsData : List<StepsData> = emptyList(),
    val isSyncing : Boolean = true,
    val isPermissionGranted : Boolean = true,
    val streakCount : String = "0",
    val motivationText : String = "",
    val todayStepsData : StepsData = StepsData()
)


