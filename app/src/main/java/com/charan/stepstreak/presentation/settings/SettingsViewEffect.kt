package com.charan.stepstreak.presentation.settings

sealed interface SettingsViewEffect {
    data class ShowToast(val message : String) : SettingsViewEffect
    data class LaunchCreateDocument(val fileName : String) : SettingsViewEffect
    data class LaunchOpenDocument(val fileType : String) : SettingsViewEffect

}