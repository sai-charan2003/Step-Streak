package com.charan.stepstreak.presentation.settings

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.NextWeek
import androidx.compose.material.icons.automirrored.filled.SendAndArchive
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.stepstreak.data.model.StartOfWeekEnums
import com.charan.stepstreak.data.model.ThemeEnum
import com.charan.stepstreak.data.repository.BackupRepo
import com.charan.stepstreak.data.repository.impl.BackupRepoImpl
import com.charan.stepstreak.presentation.settings.components.ChangeDataProviderSheet
import com.charan.stepstreak.presentation.settings.components.ChangeSyncFrequencySheet
import com.charan.stepstreak.presentation.settings.components.CustomDropDown
import com.charan.stepstreak.presentation.settings.components.SetStepGoalDialog
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    onLicenseScreenNavigate : () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val state by viewModel.settingsState.collectAsStateWithLifecycle()
    val goalsBottomSheetState = rememberModalBottomSheetState()
    val dataProviderSheetState = rememberModalBottomSheetState()
    val syncFrequencySheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    val createBackup =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument(
                BackupRepo.FILE_TYPE
            )
        ) { uri ->
            viewModel.onEvent(SettingsEvents.SaveBackup(uri))
        }

    val pickedFile =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            viewModel.onEvent(SettingsEvents.RestoreBackup(uri))
        }

    LaunchedEffect(Unit) {
        viewModel.settingsViewEffect.collectLatest { effect ->
            when (effect) {
                is SettingsViewEffect.LaunchCreateDocument -> {
                    createBackup.launch(effect.fileName)
                }

                is SettingsViewEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_LONG
                    ).show()
                }

                is SettingsViewEffect.LaunchOpenDocument -> {
                    pickedFile.launch(arrayOf(effect.fileType))
                }
            }
        }
    }

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
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(top =  padding.calculateTopPadding())
                .padding(horizontal = 16.dp)
        ) {
            item {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleSmallEmphasized,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                ListItem(
                    headlineContent = { Text("Theme") },
                    trailingContent = {
                        TextButton(onClick = {
                            viewModel.onEvent(SettingsEvents.ToggleThemeMenu)
                        }) {
                            Text(state.theme.getName())
                            CustomDropDown(
                                items = ThemeEnum.entries.map { it.getName() },
                                selectedItem = state.theme.getName(),
                                onItemSelected = { viewModel.onEvent(SettingsEvents.SetTheme(it)) },
                                isExpanded = state.showThemeMenu,
                                onDismiss = { viewModel.onEvent(SettingsEvents.ToggleThemeMenu) }

                            )
                        }
                    },
                    leadingContent = {
                        Icon(Icons.Rounded.Contrast,null)
                    },
                    modifier = Modifier.clickable {
                        viewModel.onEvent(SettingsEvents.ToggleThemeMenu)
                    }
                )
                ListItem(
                    headlineContent = { Text("Dynamic Colors") },
                    trailingContent = {
                        Switch(
                            checked = state.isDynamicColor,
                            onCheckedChange = {
                                viewModel.onEvent(SettingsEvents.SetDynamicColor(it))

                            }

                        )
                    },
                    leadingContent = {
                        Icon(Icons.Rounded.Palette,null)
                    },
                    modifier = Modifier.clickable {
                        viewModel.onEvent(SettingsEvents.SetDynamicColor(!state.isDynamicColor))
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
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
                            text = if (state.targetSteps.isNotEmpty()) "%,d".format(state.targetSteps.toLongOrNull() ?: 0) else "",
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
                ListItem(
                    headlineContent = { Text("Start of week") },
                    trailingContent = {
                        TextButton(onClick = {
                            viewModel.onEvent(SettingsEvents.ToggleStartOfWeekMenu)
                        }) {
                            Text(state.startOfWeek.getName())
                            CustomDropDown(
                                items = StartOfWeekEnums.entries.map { it.getName() },
                                selectedItem = state.startOfWeek.getName(),
                                onItemSelected = { viewModel.onEvent(SettingsEvents.SetStartOfWeek(it)) },
                                isExpanded = state.showStartOfWeekMenu,
                                onDismiss = { viewModel.onEvent(SettingsEvents.ToggleStartOfWeekMenu) }

                            )
                        }
                    },
                    leadingContent = {
                        Icon(Icons.Rounded.Today,null)
                    },
                    modifier = Modifier.clickable {
                        viewModel.onEvent(SettingsEvents.ToggleStartOfWeekMenu)
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            }
            item {
                Text(
                    text = "Backup",
                    style = MaterialTheme.typography.titleSmallEmphasized,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                ListItem(
                    headlineContent = { Text("Export Data") },
                    leadingContent = {
                        Icon(Icons.Rounded.Upload,null)
                    },
                    trailingContent = {
                        AnimatedVisibility(
                            visible = state.isExportingData
                        ) {
                            ContainedLoadingIndicator(modifier = Modifier.size(32.dp))
                        }

                    },
                    modifier = Modifier.clickable (enabled = !state.isExportingData){
                        viewModel.onEvent(SettingsEvents.OnExportData)
                    }

                )
                ListItem(
                    headlineContent = { Text("Import Data") },
                    leadingContent = {
                        Icon(Icons.Rounded.Download,null)
                    },
                    trailingContent = {
                        AnimatedVisibility(
                            visible = state.isImportingData
                        ) {
                            ContainedLoadingIndicator(modifier = Modifier.size(32.dp))
                        }

                    },
                    modifier = Modifier.clickable (enabled = !state.isImportingData){
                        viewModel.onEvent(SettingsEvents.OnImportData)
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

