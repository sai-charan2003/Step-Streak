package com.charan.stepstreak.presentation.settings

import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.stepstreak.BuildConfig
import com.charan.stepstreak.data.model.DataProviders
import com.charan.stepstreak.data.model.SyncTime
import com.charan.stepstreak.data.repository.DataStoreRepo
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.data.repository.StepsRecordRepo
import com.charan.stepstreak.data.repository.UsersSettingsRepo
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

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreRepo: DataStoreRepo,
    private val healthConnectRepo: HealthConnectRepo,
    private val stepsRecordRepo: StepsRecordRepo,
    private val usersSettingsRepo: UsersSettingsRepo
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
    }

    private fun setTargetSteps() = viewModelScope.launch(Dispatchers.IO) {
       usersSettingsRepo.getStepsTarget().collectLatest { target->
           _settingsState.update { it.copy(targetSteps = target) }

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
        if(value.length > 7) return@launch
        val numericValue = value.filter { it.isDigit() }
        val target = numericValue.toLongOrNull() ?: 0L
        if(target <=0L) return@launch
        _settingsState.update { it.copy(targetSteps = target) }
    }


    private fun onStepsDataIncrement() = viewModelScope.launch(Dispatchers.IO) {
        _settingsState.update { it.copy(targetSteps = it.targetSteps + 1) }
    }

    private fun onStepsDataDecrement() = viewModelScope.launch(Dispatchers.IO) {
        if(_settingsState.value.targetSteps == 1L) return@launch
        _settingsState.update { it.copy(targetSteps = it.targetSteps - 1) }

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




}