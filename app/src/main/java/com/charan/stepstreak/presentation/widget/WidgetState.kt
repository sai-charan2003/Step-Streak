package com.charan.stepstreak.presentation.widget

import com.charan.stepstreak.presentation.common.StepsData
import com.charan.stepstreak.presentation.common.WeeklyData

data class WidgetState(
    val streak : Int = 0,
    val motivationText : String ="",
    val currentWeekStepData: WeeklyData = WeeklyData(),
    val todayData : StepsData = StepsData()

)
