package com.charan.stepstreak.presentation.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun initData() = viewModelScope.launch(Dispatchers.IO){

        _state.update {
            it.copy(
                isPermissionGranted = healthConnectRepo.hasPermission()
            )
        }

    }


    fun onEvent(event: OnBoardingEvents) = viewModelScope.launch{
        when(event){
            OnBoardingEvents.OnChangePage -> {
                _event.emit(OnBoardingViewEffect.ScrollPage)

            }
            OnBoardingEvents.OnRequestPermission -> {
                _event.emit(OnBoardingViewEffect.RequestPermission)

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
                getDataProviders()
                initData()
            }

            OnBoardingEvents.OnBoardingComplete -> {
                dataStoreRepo.setOnBoardingStatus(true)
                _event.emit(OnBoardingViewEffect.OnBoardingComplete)

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


}