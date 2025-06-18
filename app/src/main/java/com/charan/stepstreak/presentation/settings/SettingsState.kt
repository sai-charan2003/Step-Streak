package com.charan.stepstreak.presentation.settings

import com.charan.stepstreak.data.model.DataProviders
import com.charan.stepstreak.data.model.StartOfWeekEnums
import com.charan.stepstreak.data.model.ThemeEnum

data class SettingsState(
    val targetSteps: Long = 0,
    val dataProviders: List<DataProviders> = emptyList(),
    val selectedDataProvider : DataProviders? = null,
    val showGoalsSheet : Boolean = false,
    val showDataProviderSheet : Boolean = false,
    val showFrequencySheet : Boolean = false,
    val frequency : Long = 0,
    val frequencyString : String = "",
    val appVersion : String = "",
    val isDynamicColor : Boolean = false,
    val theme : ThemeEnum = ThemeEnum.SYSTEM,
    val showThemeMenu : Boolean = false,
    val startOfWeek : StartOfWeekEnums = StartOfWeekEnums.MONDAY,
    val showStartOfWeekMenu : Boolean = false
)