package com.charan.stepstreak.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.stepstreak.data.repository.HealthConnectRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val healthConnectRepo: HealthConnectRepo
) : ViewModel() {
    private val _state = MutableStateFlow(OnBoardingState())
    val state = _state.asStateFlow()

    fun initData() = viewModelScope.launch(Dispatchers.IO){

        _state.update {
            it.copy(
                isPermissionGranted = healthConnectRepo.hasPermission()
            )
        }

    }


}