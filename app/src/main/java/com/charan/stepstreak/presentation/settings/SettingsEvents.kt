package com.charan.stepstreak.presentation.settings

import android.net.Uri
import com.charan.stepstreak.data.model.DataProviders
import com.charan.stepstreak.data.model.ThemeEnum

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
    data class SetDynamicColor(val value : Boolean) : SettingsEvents
    data class SetTheme(val theme: String) : SettingsEvents
    object ToggleThemeMenu : SettingsEvents
    data class SetStartOfWeek(val startOfWeek: String) : SettingsEvents
    object ToggleStartOfWeekMenu : SettingsEvents
    object OnExportData : SettingsEvents
    object OnImportData : SettingsEvents
    data class SaveBackup(val uri : Uri?) : SettingsEvents
    data class RestoreBackup(val uri : Uri?) : SettingsEvents

}