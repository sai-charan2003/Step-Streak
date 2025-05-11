package com.charan.stepstreak.presentation.settings

sealed interface SettingsViewEffect {
    object ToggleGoalsBottomSheet : SettingsViewEffect
    object ToggleDataProviderSheet : SettingsViewEffect
}