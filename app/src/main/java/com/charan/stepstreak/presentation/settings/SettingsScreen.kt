package com.charan.stepstreak.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.stepstreak.presentation.settings.components.ChangeDataProviderSheet
import com.charan.stepstreak.presentation.settings.components.ChangeSyncFrequencySheet
import com.charan.stepstreak.presentation.settings.components.SetStepGoalDialog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    onBackPress : () -> Unit,
    onLicenseScreenNavigate : () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val state by viewModel.settingsState.collectAsStateWithLifecycle()
    val goalsBottomSheetState = rememberModalBottomSheetState()
    val dataProviderSheetState = rememberModalBottomSheetState()
    val syncFrequencySheetState = rememberModalBottomSheetState()

    if (state.showGoalsSheet) {
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

    if (state.showDataProviderSheet) {
        ChangeDataProviderSheet(
            dataProvider = state.dataProviders,
            onValueChange = { viewModel.onEvent(SettingsEvents.OnDataProviderChange(it)) },
            onSave = { viewModel.onEvent(SettingsEvents.OnSaveDataProvider) },
            onDismiss = { viewModel.onEvent(SettingsEvents.ToggleDataProviderSheet(false)) },
            sheetState = dataProviderSheetState
        )
    }

    if (state.showFrequencySheet) {
        ChangeSyncFrequencySheet(
            currentFrequency = state.frequency,
            onValueChange = { viewModel.onEvent(SettingsEvents.OnChangeFrequency(it)) },
            onSave = { viewModel.onEvent(SettingsEvents.OnSaveFrequency) },
            onDismiss = { viewModel.onEvent(SettingsEvents.ToggleFrequencySheet(false)) },
            sheetState = syncFrequencySheetState
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text("Settings") },
                scrollBehavior = scroll,
                navigationIcon = {
                    IconButton(
                        onClick = { onBackPress.invoke() },
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                Text(
                    text = "Health Data",
                    style = MaterialTheme.typography.titleSmallEmphasized,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                ListItem(
                    headlineContent = { Text("Daily Steps Goal") },
                    trailingContent = {
                        Text(
                            text = "%,d".format(state.targetSteps),
                            style = MaterialTheme.typography.bodyLargeEmphasized
                        )
                    },
                    leadingContent = {
                        Icon(Icons.AutoMirrored.Filled.DirectionsWalk,null)
                    },
                    modifier = Modifier.clickable {
                        viewModel.onEvent(SettingsEvents.ToggleGoalsSheet(true))
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            }

            item {
                Text(
                    text = "About App",
                    style = MaterialTheme.typography.titleSmallEmphasized,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                ListItem(
                    headlineContent = { Text("App Version") },
                    leadingContent = {
                        Icon(Icons.Rounded.Code,null)
                    },
                    trailingContent = {
                        Text(
                            text = state.appVersion,
                            style = MaterialTheme.typography.bodyLargeEmphasized
                        )
                    }
                )

                ListItem(
                    headlineContent = { Text("Open Source Licenses") },
                    leadingContent = {
                        Icon(Icons.Rounded.WorkspacePremium,null)
                    },
                    modifier = Modifier.clickable {
                        onLicenseScreenNavigate.invoke()
                    }
                )
            }
        }
    }
}

