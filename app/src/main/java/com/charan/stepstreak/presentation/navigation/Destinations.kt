package com.charan.stepstreak.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Destinations : NavKey {
    @Serializable
    object OnBoardingScreenNav : Destinations()

    @Serializable
    object LicenseScreenNav : Destinations()

    @Serializable
    object BottomNavScreenNav : Destinations()
}

sealed class BottomNavScreenNav : NavKey {
    @Serializable
    object HomeScreenNav : BottomNavScreenNav()

    @Serializable
    object SettingsScreenNav : BottomNavScreenNav()
}