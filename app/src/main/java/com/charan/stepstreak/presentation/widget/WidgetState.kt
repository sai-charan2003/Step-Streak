package com.charan.stepstreak.presentation.widget

import com.charan.stepstreak.presentation.common.StepsData

data class WidgetState(
    val streak : Int = 0,
    val motivationText : String ="",
    val currentWeekStepData: List<StepsData> =emptyList(),
    val todayData : StepsData = StepsData()

)
