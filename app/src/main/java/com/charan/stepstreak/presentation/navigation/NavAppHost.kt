package com.charan.stepstreak.presentation.navigation

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.charan.stepstreak.presentation.home.HomeScreen
import com.charan.stepstreak.presentation.onboarding.OnBoardingScreen
import com.charan.stepstreak.presentation.settings.LicensesScreen
import com.charan.stepstreak.presentation.settings.SettingsScreen

@Composable
fun NavAppHost(
    isOnBoardingCompleted : Boolean
) {
    val backStack = rememberNavBackStack(HomeScreenNav)
    LaunchedEffect(isOnBoardingCompleted) {
        if(isOnBoardingCompleted == false){
            backStack.removeLastOrNull()
            backStack.add(OnBoardingScreenNav)
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        transitionSpec = {
            (fadeIn() + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                initialOffset = { 100 },
                animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
            )) togetherWith
                    (fadeOut() + slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        targetOffset = { -100 },
                        animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
                    ))
        },
        popTransitionSpec = {
            (fadeIn() + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                initialOffset = { -100 },
                animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
            )) togetherWith
                    (fadeOut() + slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        targetOffset = { 100 },
                        animationSpec = (tween(easing = LinearEasing, durationMillis = 200))
                    ))

        },
        predictivePopTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })

        },
        entryProvider = { key ->
            when (key) {
                is HomeScreenNav -> NavEntry(key) {
                    HomeScreen(
                        onSettingNavigate = {
                            backStack.add(SettingsScreenNav)
                        }
                    )
                }

                is SettingsScreenNav -> NavEntry(key) {
                    SettingsScreen(
                        onBackPress = {
                            backStack.removeLastOrNull()
                        },
                        onLicenseScreenNavigate = {
                            backStack.add(LicenseDataScreenNav)
                        }
                    )
                }
                is OnBoardingScreenNav -> NavEntry(key) {
                    OnBoardingScreen(
                        onHomeScreenNavigate = {
                            backStack.removeLastOrNull()
                            backStack.add(HomeScreenNav)
                        }
                    )
                }
                is LicenseDataScreenNav -> NavEntry(key){
                    LicensesScreen(onBackPress = {
                        Log.d("TAG", "NavAppHost: $backStack")
                        backStack.removeLastOrNull()
                    })
                }

                else -> NavEntry(key) { Text("Unknown route") }
            }
        },
    )
}