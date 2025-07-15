package com.charan.stepstreak.presentation.common
import com.charan.stepstreak.utils.DateUtils

data class PeriodStepsData(
    val averageSteps: Long = 0L,
    val stepsData : List<StepsData> = emptyList(),
    val totalSteps : Long = 0L,
    val periodLabel : String = DateUtils.getCurrentMonthWithYear(),
    val highestSteps : StepsData = StepsData()

)
