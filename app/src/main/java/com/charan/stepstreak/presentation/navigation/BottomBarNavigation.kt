package com.charan.stepstreak.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.charan.stepstreak.presentation.home.HomeScreen
import com.charan.stepstreak.presentation.settings.SettingsScreen

@Composable
fun BottomNavScreen(
    onLicenceNavigation : () -> Unit

) {
    val bottomBackStack = rememberNavBackStack(BottomNavScreenNav.HomeScreenNav)
    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }
    var previousSelectedItem by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(selectedItem) {
        when (BottomNavItem.entries[selectedItem]) {
            BottomNavItem.HOME -> {
                bottomBackStack.clear()
                bottomBackStack.add(BottomNavScreenNav.HomeScreenNav)
            }

            BottomNavItem.SETTINGS -> {
                bottomBackStack.clear()
                bottomBackStack.add(BottomNavScreenNav.SettingsScreenNav)
            }

            else -> {}
        }
    }


    NavigationSuiteScaffold(
        navigationItemVerticalArrangement = Arrangement.Center,
        navigationItems = {
            BottomNavItem.entries.forEachIndexed { index, navItem ->
                NavigationSuiteItem(
                    modifier = Modifier.padding(top = 5.dp),
                    selected = index == selectedItem,
                    onClick = {
                        previousSelectedItem = selectedItem
                        selectedItem = index
                    },
                    icon = {
                        Icon(
                            imageVector = if (index == selectedItem) navItem.selectedIcon else navItem.unselectedIcon,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(navItem.title)
                    }
                )

            }

        },

    ) {
        NavDisplay(
            modifier = Modifier,
            backStack = bottomBackStack,
            onBack = { bottomBackStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSavedStateNavEntryDecorator(),
                rememberSavedStateNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            transitionSpec = {
                val forward = selectedItem > previousSelectedItem

                val enterTransition = slideInHorizontally(
                    initialOffsetX = { if (forward) it else -it },
                    animationSpec = tween(durationMillis = 250, easing = LinearOutSlowInEasing)
                )

                val exitTransition = slideOutHorizontally(
                    targetOffsetX = { if (forward) -it else it },
                    animationSpec = tween(durationMillis = 250, easing = LinearOutSlowInEasing)
                )

                enterTransition togetherWith exitTransition
            },


                    entryProvider = { key ->
                when (key) {
                    is BottomNavScreenNav.HomeScreenNav -> NavEntry(key) {
                        HomeScreen()
                    }

                    is BottomNavScreenNav.SettingsScreenNav -> NavEntry(key) {
                        SettingsScreen(
                            onLicenseScreenNavigate = {
                                onLicenceNavigation.invoke()
                            }
                        )
                    }

                    else -> NavEntry(key) {
                        Text("Unknown route")
                    }
                }

            }

        )

    }


}

enum class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,

    ),
    HISTORY(
        title = "History",
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart,
    ),
    SETTINGS(
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    )
}
