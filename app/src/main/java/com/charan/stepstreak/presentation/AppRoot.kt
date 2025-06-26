package com.charan.stepstreak.presentation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.charan.stepstreak.navigationItem
import com.charan.stepstreak.presentation.home.HomeScreen
import com.charan.stepstreak.presentation.navigation.BottomNavScreenNav
import com.charan.stepstreak.presentation.navigation.Destinations
import com.charan.stepstreak.presentation.settings.SettingsScreen

@Composable
fun BottomNavScreen(
    onLicienceNavigation : () -> Unit

) {
    val bottomBackStack = rememberNavBackStack(BottomNavScreenNav.HomeScreenNav)
    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }

    LaunchedEffect(selectedItem) {
        when(selectedItem){
            0-> {
                bottomBackStack.add(BottomNavScreenNav.HomeScreenNav)
            }
            2-> {
                bottomBackStack.add(BottomNavScreenNav.SettingsScreenNav)
            }
        }
    }
    Scaffold(
        bottomBar = {
            NavigationBar {
                navigationItem.forEachIndexed {index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                        },
                        icon = {
                            Icon(
                                imageVector = if(selectedItem == index) item.selectedItem else item.unSelectedItem,
                                contentDescription = "Navigation Icon ${item.title}"
                            )
                        },
                        label = {
                            Text(item.title)
                        }
                    )
                }
            }


        }
    ) { padding->
        NavDisplay(
            modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
            backStack = bottomBackStack,
            onBack = { bottomBackStack.removeLastOrNull()},
            entryProvider = { key->
                when(key){
                    is BottomNavScreenNav.HomeScreenNav -> NavEntry(key){
                        HomeScreen()
                    }
                    is BottomNavScreenNav.SettingsScreenNav -> NavEntry(key){
                        SettingsScreen(
                            onLicenseScreenNavigate = {
                                onLicienceNavigation.invoke()
                            }
                        )
                    }
                    else -> NavEntry(key){
                        Text("Unknown route")
                    }
                }

            }

        )
    }
}