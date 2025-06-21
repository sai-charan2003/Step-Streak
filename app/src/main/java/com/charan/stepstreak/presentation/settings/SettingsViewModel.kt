package com.charan.stepstreak.presentation.settings

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.stepstreak.BuildConfig
import com.charan.stepstreak.data.model.DataProviders
import com.charan.stepstreak.data.model.StartOfWeekEnums
import com.charan.stepstreak.data.model.SyncTime
import com.charan.stepstreak.data.model.ThemeEnum
import com.charan.stepstreak.data.repository.BackupRepo
import com.charan.stepstreak.data.repository.DataStoreRepo
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.data.repository.StepsRecordRepo
import com.charan.stepstreak.data.repository.UsersSettingsRepo
import com.charan.stepstreak.presentation.settings.SettingsViewEffect.*
import com.charan.stepstreak.utils.Constants
import com.charan.stepstreak.utils.ProcessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val MAX_TARGET_LENGTH = 7
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreRepo: DataStoreRepo,
    private val healthConnectRepo: HealthConnectRepo,
    private val stepsRecordRepo: StepsRecordRepo,
    private val usersSettingsRepo: UsersSettingsRepo,
    private val backupRepo: BackupRepo
) : ViewModel() {
    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState = _settingsState.asStateFlow()
    private val _settingsViewEffect = MutableSharedFlow<SettingsViewEffect>()
    val settingsViewEffect = _settingsViewEffect.asSharedFlow()
    init {
        setTargetSteps()
        getAllDataProviders()
        setSelectedDataProvider()
//        setSyncFrequency()
        setAppVersion()
        getDynamicColorStatus()
        getThemeData()
        getStartOfWeek()
    }

    private fun setTargetSteps() = viewModelScope.launch(Dispatchers.IO) {
       usersSettingsRepo.getStepsTarget().collectLatest { target->
           _settingsState.update { it.copy(targetSteps = target.toString()) }

       }
    }

    private fun setSelectedDataProvider() = viewModelScope.launch (Dispatchers.IO){
        _settingsState.collectLatest {
            _settingsState.update { it.copy(selectedDataProvider = it.dataProviders.firstOrNull { it.isConnected == true }) }

        }
    }

    private fun setAppVersion() = viewModelScope.launch (Dispatchers.IO){
        _settingsState.update { it.copy(appVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})") }
    }

    private fun setSyncFrequency() = viewModelScope.launch (Dispatchers.IO){
        dataStoreRepo.syncFrequency.collectLatest { frequency->
            _settingsState.update {
                it.copy(
                    frequency = frequency,
                    frequencyString = SyncTime.entries.find { it.minutes == frequency }?.getName() ?: ""
                )
            }
        }
    }

    private fun getAllDataProviders() =  viewModelScope.launch (Dispatchers.IO){
        healthConnectRepo.getOriginProviders().collectLatest { providers->
            when(providers){
                is ProcessState.Error ->{}
                ProcessState.Loading ->{}
                is ProcessState.Success<*> -> {
                    _settingsState.update { it.copy(dataProviders = providers.data as List<DataProviders>) }

                }
            }
        }
    }

    fun onEvent(event : SettingsEvents) = viewModelScope.launch (Dispatchers.IO) {
        when (event) {
            is SettingsEvents.ToggleDataProviderSheet -> {
                _settingsState.update { it.copy(showDataProviderSheet = event.shouldShow) }
            }

            is SettingsEvents.ToggleGoalsSheet -> {
                _settingsState.update { it.copy(showGoalsSheet = event.shouldShow) }
                setTargetSteps()
            }

            SettingsEvents.OnSaveStepsTarget -> {
                saveTargetSteps()

            }
            SettingsEvents.OnStepsTargetDecrement -> {
                onStepsDataDecrement()

            }
            SettingsEvents.OnStepsTargetIncrement -> {
                onStepsDataIncrement()

            }

            is SettingsEvents.OnStepsTargetValueChange -> {
                onStepTargetValueChange(event.value)
            }

            is SettingsEvents.OnDataProviderChange -> {
                onDataProviderChange(event.provider)
            }

            SettingsEvents.OnSaveDataProvider -> {
               saveDataProvider()
            }

            is SettingsEvents.OnChangeFrequency -> {
                onSyncFrequencyChange(event.frequency)


            }
            SettingsEvents.OnSaveFrequency -> {
                onSaveSyncFrequency()

            }

            is SettingsEvents.ToggleFrequencySheet -> {
                _settingsState.update { it.copy(showFrequencySheet = event.shouldShow) }
            }

            is SettingsEvents.SetDynamicColor -> {
                setDynamicColorStatus(event.value)

            }

            is SettingsEvents.SetTheme -> {
                setTheme(event.theme)
            }

            SettingsEvents.ToggleThemeMenu -> {
                _settingsState.update { it.copy(showThemeMenu = !_settingsState.value.showThemeMenu) }
            }

            is SettingsEvents.SetStartOfWeek -> setStartOfWeek(event.startOfWeek)
            SettingsEvents.ToggleStartOfWeekMenu -> {
                _settingsState.update { it.copy(showStartOfWeekMenu = !_settingsState.value.showStartOfWeekMenu) }
            }

            SettingsEvents.OnExportData -> {
                _settingsViewEffect.emit(LaunchCreateDocument(backupRepo.fileName))

            }
            SettingsEvents.OnImportData -> {
                _settingsViewEffect.emit(LaunchOpenDocument(BackupRepo.FILE_TYPE))

            }

            is SettingsEvents.SaveBackup -> {
                backupData(event.uri)
            }

            is SettingsEvents.RestoreBackup -> {
                importData(event.uri)


            }
        }
    }

    private fun saveDataProvider() = viewModelScope.launch(Dispatchers.IO){
        if(dataStoreRepo.dataProviders.first() != _settingsState.value.selectedDataProvider?.packageName.toString()){
            stepsRecordRepo.deleteAllStepRecords()
        }
        dataStoreRepo.setDataProviders(_settingsState.value.selectedDataProvider?.packageName.toString())
        _settingsState.update { it.copy(showDataProviderSheet = false) }
    }

    private fun onStepTargetValueChange(value: String) = viewModelScope.launch(Dispatchers.IO) {
        if(value.length > MAX_TARGET_LENGTH) return@launch
        val numericValue = value.filter { it.isDigit() }.ifEmpty { null }
        val target = numericValue?.toString() ?: ""
        _settingsState.update { it.copy(targetSteps = target) }
    }


private fun onStepsDataIncrement() = viewModelScope.launch(Dispatchers.IO) {
    _settingsState.update {
        val currentSteps = it.targetSteps.toLongOrNull() ?: 0L
        val nextSteps = if (currentSteps % 500 == 0L) currentSteps + 500 else ((currentSteps / 500) + 1) * 500
        if(nextSteps.toString().length > MAX_TARGET_LENGTH) {
            return@launch
        }
        it.copy(targetSteps = nextSteps.toString())
    }
}

    private fun onStepsDataDecrement() = viewModelScope.launch(Dispatchers.IO) {
        _settingsState.update {
            val currentSteps = it.targetSteps.toLongOrNull() ?: 0L
            if(currentSteps <=0) {
                it.copy(targetSteps = "0")
                return@launch
            }
            val nextSteps = if (currentSteps % 500 == 0L) currentSteps - 500 else ((currentSteps / 500) - 1).coerceAtLeast(0L) * 500
            it.copy(targetSteps = nextSteps.toString())
        }

    }
    private fun saveTargetSteps() = viewModelScope.launch(Dispatchers.IO) {
        usersSettingsRepo.insertSetting(setting = Constants.STEPS_TARGET_SETTING, value = _settingsState.value.targetSteps.toString())
        _settingsState.update { it.copy(showGoalsSheet = false) }
    }

    private fun onDataProviderChange(provider: DataProviders) = viewModelScope.launch(Dispatchers.IO) {
        val updatedProviders = _settingsState.value.dataProviders.map { currentProvider ->
            currentProvider.copy(
                isConnected = currentProvider.packageName == provider.packageName
            )
        }

        _settingsState.update {
            it.copy(dataProviders = updatedProviders)
        }
    }

    fun onSyncFrequencyChange(frequency: Long) = viewModelScope.launch(Dispatchers.IO) {
        _settingsState.update { it.copy(frequency = frequency) }
    }
    fun onSaveSyncFrequency() = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepo.setSyncFrequency(_settingsState.value.frequency)
        _settingsState.update { it.copy(showFrequencySheet = false) }
    }

    fun getDynamicColorStatus() = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepo.isDynamicColor.collectLatest { isDynamicColor ->
            _settingsState.update { it.copy(isDynamicColor = isDynamicColor) }
        }
    }

    fun setDynamicColorStatus(value : Boolean) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepo.changeDynamicColorStatus(value)
    }

    fun getThemeData() = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepo.theme.collectLatest { themeData ->
            _settingsState.update { it.copy(theme = themeData) }
        }
    }

    fun setTheme(theme: String) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepo.setTheme(ThemeEnum.fromName(theme))
        _settingsState.update { it.copy(showThemeMenu = false) }
    }

    fun getStartOfWeek() = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepo.startOfWeek.collectLatest { startOfWeek ->
            _settingsState.update { it.copy(startOfWeek = startOfWeek) }
        }
    }


    fun setStartOfWeek(startOfWeek: String) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepo.setStartOfWeek(StartOfWeekEnums.fromName(startOfWeek))
        _settingsState.update { it.copy(showStartOfWeekMenu = false) }
    }

    private fun backupData(uri: Uri?) = viewModelScope.launch(Dispatchers.IO) {
        backupRepo.createBackup(uri).collectLatest { state->
            when(state){
                is ProcessState.Error -> {
                    _settingsViewEffect.emit(ShowToast(state.message))
                    _settingsState.update { it.copy(isExportingData = false) }
                }
                ProcessState.Loading -> {
                    _settingsState.update { it.copy(isExportingData = true) }
                }
                is ProcessState.Success<*> -> {
                    _settingsState.update { it.copy(isExportingData = false) }
                    _settingsViewEffect.emit(ShowToast("Backup created successfully"))
                }
            }

        }
    }

    private fun importData(uri: Uri?) = viewModelScope.launch(Dispatchers.IO) {
        backupRepo.restoreBackup(uri).collectLatest { state->
            when(state){
                is ProcessState.Error -> {
                    _settingsViewEffect.emit(ShowToast(state.message))
                    _settingsState.update { it.copy(isImportingData = false) }
                }
                ProcessState.Loading -> {
                    _settingsState.update { it.copy(isImportingData = true) }
                }
                is ProcessState.Success<*> -> {
                    _settingsViewEffect.emit(ShowToast("Backup restored successfully"))
                    _settingsState.update { it.copy(isImportingData = false) }
                }
            }
        }
    }




}