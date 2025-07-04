package com.charan.stepstreak.presentation.stats

import com.charan.stepstreak.data.model.StatType
import com.charan.stepstreak.presentation.common.PeriodStepsData
import com.charan.stepstreak.presentation.home.GraphData
import com.charan.stepstreak.utils.DateUtils

data class StatsState(
    val isLoading : Boolean = true,
    val stepData : PeriodStepsData = PeriodStepsData(),
    val selectedStatType : StatType = StatType.MONTHLY,
    val graphData : List<GraphData> = emptyList(),
    val currentMonth : String = "",
    val currentWeek : Pair<String, String> = Pair("",""),
    val formatedWeek : String = ""
)