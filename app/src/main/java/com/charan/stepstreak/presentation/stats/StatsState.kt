package com.charan.stepstreak.presentation.stats

import com.charan.stepstreak.data.model.StatType
import com.charan.stepstreak.presentation.common.PeriodStepsData

data class StatsState(
    val isLoading : Boolean = true,
    val weeklySteps : PeriodStepsData = PeriodStepsData(),
    val monthlyData : PeriodStepsData = PeriodStepsData(),
    val selectedStatType : StatType = StatType.MONTHLY,
)