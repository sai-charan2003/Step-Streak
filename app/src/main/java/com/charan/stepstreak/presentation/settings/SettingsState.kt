package com.charan.stepstreak.presentation.settings

import com.charan.stepstreak.data.model.DataProviders
import com.charan.stepstreak.data.model.ThemeEnum

data class SettingsState(
    val targetSteps: Long = 0,
    val dataProviders: List<DataProviders> = emptyList(),
    val selectedDataProvider : DataProviders? = null,
    var showGoalsSheet : Boolean = false,
    var showDataProviderSheet : Boolean = false,
    var showFrequencySheet : Boolean = false,
    var frequency : Long = 0,
    var frequencyString : String = "",
    val appVersion : String = "",
    val isDynamicColor : Boolean = false,
    val theme : ThemeEnum = ThemeEnum.SYSTEM,
    val showThemeMenu : Boolean = false,
)