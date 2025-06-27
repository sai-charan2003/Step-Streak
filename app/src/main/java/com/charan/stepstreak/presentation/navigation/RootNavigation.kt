package com.charan.stepstreak.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.charan.stepstreak.presentation.onboarding.OnBoardingScreen
import com.charan.stepstreak.presentation.settings.licenseScreen.LicensesScreen

@Composable
fun RootNavigation(
    isOnBoardingCompleted : Boolean
) {
    val backStack  = rememberNavBackStack(Destinations.BottomNavScreenNav)
    LaunchedEffect(isOnBoardingCompleted) {
        if(isOnBoardingCompleted == false){
            backStack.removeLastOrNull()
            backStack.add(Destinations.OnBoardingScreenNav)
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = {backStack.removeLastOrNull()},
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = { key ->
            when (key) {
                is Destinations.BottomNavScreenNav -> NavEntry(key){
                    BottomNavScreen(
                        onLicenceNavigation = {
                            backStack.add(Destinations.LicenseScreenNav)
                        }
                    )

                }
                is Destinations.OnBoardingScreenNav -> NavEntry(key) {
                    OnBoardingScreen(
                        onHomeScreenNavigate = {
                            backStack.removeLastOrNull()
                            backStack.add(Destinations.BottomNavScreenNav)
                        }
                    )
                }
                is Destinations.LicenseScreenNav -> NavEntry(key){
                    LicensesScreen(
                        onBackPress = {
                            backStack.removeLastOrNull()
                        }
                    )
                }
                else -> NavEntry(key) { Text("Unknown route") }
            }
        },
    )
}