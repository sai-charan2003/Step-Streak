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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.charan.stepstreak.presentation.home.HomeScreen
import com.charan.stepstreak.presentation.onboarding.OnBoardingScreen
import com.charan.stepstreak.presentation.settings.licenseScreen.LicensesScreen
import com.charan.stepstreak.presentation.settings.SettingsScreen

@Composable
fun NavAppHost(
    isOnBoardingCompleted : Boolean,
    topLevelBackStack: TopLevelBackStack<Any>
) {
    LaunchedEffect(isOnBoardingCompleted) {
        if(isOnBoardingCompleted == false){
            topLevelBackStack.removeLast()
            topLevelBackStack.updateTopStack(Destinations.OnBoardingScreenNav)
        }
    }

    NavDisplay(
        backStack = topLevelBackStack.backStack,
        onBack = {topLevelBackStack.removeLast() },
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = { key ->
            when (key) {
                is Destinations.OnBoardingScreenNav -> NavEntry(key) {
                    OnBoardingScreen(
                        onHomeScreenNavigate = {
                            topLevelBackStack.removeLast()
                            topLevelBackStack.updateTopStack(Destinations.BottomNavScreenNav)
                        }
                    )
                }
                else -> NavEntry(key) { Text("Unknown route") }
            }
        },
    )
}

class TopLevelBackStack<T : Any>(startKey : T) {
    val backStack = mutableStateListOf(startKey)

    private val topLevelStacks : LinkedHashMap<T, SnapshotStateList<T>> = linkedMapOf(
        startKey to mutableStateListOf(startKey)
    )
    var topLevelKey by mutableStateOf(startKey)
        private set

    private fun updateBackStack(){
        backStack.apply {
            clear()
            addAll(topLevelStacks.flatMap { it.value })
        }
    }

    fun updateTopStack(key: T){
        if (topLevelStacks[key] == null){
            topLevelStacks.put(key, mutableStateListOf(key))
        } else {

            topLevelStacks.apply {
                remove(key)?.let {
                    put(key, it)
                }
            }
        }
        topLevelKey = key
        updateBackStack()
    }
    fun add(key: T){
        topLevelStacks[topLevelKey]?.add(key)
        updateBackStack()
    }
    fun removeLast() {
        val removed = topLevelStacks[topLevelKey]?.removeLastOrNull()
        if (topLevelStacks[topLevelKey]?.isEmpty() == true) {
            topLevelStacks.remove(removed)
            topLevelKey = topLevelStacks.keys.last()
        }
        updateBackStack()
    }




}