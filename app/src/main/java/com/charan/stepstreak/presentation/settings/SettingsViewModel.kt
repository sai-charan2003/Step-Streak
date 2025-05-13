package com.charan.stepstreak.presentation.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.stepstreak.data.model.DataProviders
import com.charan.stepstreak.data.repository.DataStoreRepo
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.data.repository.StepsRecordRepo
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
    private val stepsRecordRepo: StepsRecordRepo
) : ViewModel() {
    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState = _settingsState.asStateFlow()
    private val _settingsViewEffect = MutableSharedFlow<SettingsViewEffect>()
    val settingsViewEffect = _settingsViewEffect.asSharedFlow()
    init {
        setTargetSteps()
        getAllDataProviders()
        setSelectedDataProvider()
    }

    private fun setTargetSteps() = viewModelScope.launch(Dispatchers.IO) {
       dataStoreRepo.targetSteps.collectLatest { targetSteps->
           _settingsState.update { it.copy(targetSteps = targetSteps.toLong()) }
       }
    }

    private fun setSelectedDataProvider() = viewModelScope.launch (Dispatchers.IO){
        _settingsState.collectLatest {
            _settingsState.update { it.copy(selectedDataProvider = it.dataProviders.firstOrNull { it.isConnected == true }) }

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
        dataStoreRepo.setTargetSteps(_settingsState.value.targetSteps.toString())
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




}