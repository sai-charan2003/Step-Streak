package com.charan.stepstreak.presentation.onboarding

import com.charan.stepstreak.data.model.DataProviders

data class OnBoardingState(
    val isPermissionGranted : Boolean = false,
    val dataProviders: List<DataProviders> = emptyList()
)

