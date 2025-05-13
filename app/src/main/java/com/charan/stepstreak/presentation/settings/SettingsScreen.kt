package com.charan.stepstreak.presentation.settings

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navAppHost : NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
){
    val coroutineScope = rememberCoroutineScope()
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val viewEffects by viewModel.settingsViewEffect.collectAsStateWithLifecycle(null)
    val state by viewModel.settingsState.collectAsState()
    val goalsBottomSheetState = rememberModalBottomSheetState()
    val dataProviderSheetState = rememberModalBottomSheetState()
    var showSteps by remember { mutableStateOf(false) }
    var showGoalsSheet by remember { mutableStateOf(false) }
    LaunchedEffect(viewEffects) {
        when(viewEffects){
            null -> Unit
            SettingsViewEffect.ToggleDataProviderSheet -> {

            }
            SettingsViewEffect.ToggleGoalsBottomSheet -> {
                showGoalsSheet =!showGoalsSheet

            }
        }
    }
    if(state.showGoalsSheet){
        SetStepGoalDialog(
            targetSteps = state.targetSteps,
            onIncrement = { viewModel.onEvent(SettingsEvents.OnStepsTargetIncrement) },
            onDecrement = { viewModel.onEvent(SettingsEvents.OnStepsTargetDecrement) },
            onValueChange = { viewModel.onEvent(SettingsEvents.OnStepsTargetValueChange(it)) },
            onSave = { viewModel.onEvent(SettingsEvents.OnSaveStepsTarget) },
            onDismiss = { viewModel.onEvent(SettingsEvents.ToggleGoalsSheet(false)) },
            sheetState = goalsBottomSheetState

        )

    }

    if(state.showDataProviderSheet){
        ChangeDataProviderSheet(
            dataProvider = state.dataProviders,
            onValueChange = { viewModel.onEvent(SettingsEvents.OnDataProviderChange(it)) },
            onSave = { viewModel.onEvent(SettingsEvents.OnSaveDataProvider) },
            onDismiss = { viewModel.onEvent(SettingsEvents.ToggleDataProviderSheet(false)) },
            sheetState = dataProviderSheetState

        )
    }

    Scaffold (
        modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Settings") },
                scrollBehavior = scroll,
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navAppHost.popBackStack()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }

    ){ padding->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(top = 10.dp, start = 20.dp)
        ) {

            item{
                Text(
                    "Goals",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),

                )

                ListItem(
                    headlineContent = { Text("Daily Steps Goal")},
                    trailingContent = {
                        Text(
                            text = "%,d".format(state.targetSteps),
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    modifier = Modifier.clickable{
                        viewModel.onEvent(SettingsEvents.ToggleGoalsSheet(true))

                    }
                )
                ListItem(
                    headlineContent = { Text("Health Provider")},
                    trailingContent = { Text(state.selectedDataProvider?.name ?: "")},
                    modifier = Modifier.clickable{
                        viewModel.onEvent(SettingsEvents.ToggleDataProviderSheet(true))

                    }

                )
                ListItem(
                    headlineContent = { Text("Frequency Sync")},
                    trailingContent = { Text("15 mins")}
                )
            }


        }

    }

}