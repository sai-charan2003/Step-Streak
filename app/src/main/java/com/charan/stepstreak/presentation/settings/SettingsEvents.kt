package com.charan.stepstreak.presentation.settings

import com.charan.stepstreak.data.model.DataProviders

sealed interface SettingsEvents {
    data class ToggleDataProviderSheet(var shouldShow : Boolean) : SettingsEvents
    data class ToggleGoalsSheet(var shouldShow : Boolean) : SettingsEvents
    data class ToggleFrequencySheet(var shouldShow : Boolean) : SettingsEvents
    object OnStepsTargetIncrement : SettingsEvents
    object OnStepsTargetDecrement : SettingsEvents
    object OnSaveStepsTarget : SettingsEvents
    data class OnStepsTargetValueChange(val value : String) : SettingsEvents
    data class OnDataProviderChange(val provider : DataProviders) : SettingsEvents
    data object OnSaveDataProvider : SettingsEvents
    data object OnSaveFrequency : SettingsEvents
    data class OnChangeFrequency(val frequency : Long) : SettingsEvents


}