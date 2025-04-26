package com.charan.stepstreak.presentation.home

data class HomeState(
    val stepsData : List<StepsData> = emptyList(),
    val isSyncing : Boolean = true,
    val isPermissionGranted : Boolean = true,
)

data class StepsData(
    val steps : Long = 0L,
    val date : String = "",
    val targetSteps : Long = 10000,
)
