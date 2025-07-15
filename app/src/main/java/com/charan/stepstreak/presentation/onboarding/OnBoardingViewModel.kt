package com.charan.stepstreak.presentation.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.charan.stepstreak.data.model.DataProviders
import com.charan.stepstreak.data.repository.DataStoreRepo
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.utils.ProcessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class OnBoardingViewModel @Inject constructor(
    private val healthConnectRepo: HealthConnectRepo,
    private val dataStoreRepo: DataStoreRepo
) : ViewModel() {
    private val _state = MutableStateFlow(OnBoardingState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<OnBoardingViewEffect>()
    val effects = _event.asSharedFlow()
    init {
        initData()
        getDataProviders()
    }

    private fun initData() = viewModelScope.launch(Dispatchers.IO){
        _state.update {
            it.copy(
                isPermissionGranted = healthConnectRepo.hasPermission()
            )
        }

    }


    fun onEvent(event: OnBoardingEvents) = viewModelScope.launch{
        when(event){
            is OnBoardingEvents.OnChangePage -> {
                _state.update {
                    it.copy(
                        currentPage = event.page
                    )
                }


            }
            OnBoardingEvents.OnRequestPermission -> {
                onRequestPermission()
            }
            is OnBoardingEvents.OnSelectProvider -> {
                val updatedProviders = _state.value.dataProviders.map { provider ->
                    provider.copy(
                        isConnected = provider.packageName == event.provider.packageName
                    )
                }
                _state.update {
                    it.copy(
                        dataProviders = updatedProviders
                    )
                }
                dataStoreRepo.setDataProviders(event.provider.packageName)
            }

            OnBoardingEvents.OnPermissionGranted -> {
                onPermissionGranted()
            }

            OnBoardingEvents.OnNextButtonClick -> {
                onNextButtonClick()
            }

            OnBoardingEvents.OpenHealthConnectPermissionSettings -> {
                healthConnectRepo.openSettingsPermission()
            }

            OnBoardingEvents.OnPermissionDenied -> {
                onRequestDenied()
            }
        }
    }

    private fun getDataProviders() = viewModelScope.launch {
        healthConnectRepo.getOriginProviders().collectLatest { state->
            when(state){
                is ProcessState.Error -> {

                }
                ProcessState.Loading -> {

                }
                is ProcessState.Success<*> -> {
                    _state.update {
                        it.copy(
                            dataProviders = state.data as List<DataProviders>
                        )
                    }
                }
            }

        }
    }

    private fun onRequestPermission() =viewModelScope.launch{
        if(healthConnectRepo.hasPermission()){
            _state.update {
                it.copy(isPermissionGranted = true)
            }
            return@launch
        }
        if(healthConnectRepo.hasHealthConnectClient()){
            if(dataStoreRepo.permissionDeniedCount.first()  >=2){
                healthConnectRepo.openSettingsPermission()
                return@launch
            }
            _event.emit(OnBoardingViewEffect.RequestPermission)
        } else{
            _event.emit(OnBoardingViewEffect.InstallHealthConnect)
            healthConnectRepo.installHealthConnect()

        }
    }

    private fun onRequestDenied() = viewModelScope.launch {
        dataStoreRepo.incrementPermissionDeniedCount()
    }

    private fun onPermissionGranted() = viewModelScope.launch {
        getDataProviders()
        initData()
        dataStoreRepo.resetPermissionDeniedCount()
    }

    private fun onNextButtonClick() = viewModelScope.launch {
        if(_state.value.currentPage == 0){
            _event.emit(OnBoardingViewEffect.ScrollPage(1))
            _state.update {
                it.copy(
                    currentPage =  1
                )
            }
            return@launch
        }
        if(_state.value.currentPage == 1 && !_state.value.isPermissionGranted){
            onRequestPermission()
            return@launch
        }
        if(_state.value.currentPage == 1 && _state.value.isPermissionGranted){
            _event.emit(OnBoardingViewEffect.OnBoardingComplete)
            dataStoreRepo.setOnBoardingStatus(true)
            return@launch
        }

    }


}