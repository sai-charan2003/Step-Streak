package com.charan.stepstreak.presentation.settings

import com.charan.stepstreak.data.model.DataProviders

data class SettingsState(
    val targetSteps: Long = 0,
    val dataProviders: List<DataProviders> = emptyList(),
    val selectedDataProvider : DataProviders? = null,
    var showGoalsSheet : Boolean = false,
    var showDataProviderSheet : Boolean = false,
    var showFrequencySheet : Boolean = false,
    var frequency : Long = 0,
)