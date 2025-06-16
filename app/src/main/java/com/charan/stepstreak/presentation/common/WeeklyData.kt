package com.charan.stepstreak.presentation.common


data class WeeklyData(
    val averageSteps: Long = 0L,
    val stepsData : List<StepsData> = emptyList()

)
