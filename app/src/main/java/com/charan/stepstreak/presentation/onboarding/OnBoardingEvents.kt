package com.charan.stepstreak.presentation.onboarding

import com.charan.stepstreak.data.model.DataProviders

sealed interface OnBoardingEvents {

    object OnRequestPermission : OnBoardingEvents

    data class OnChangePage(val page : Int) : OnBoardingEvents

    data class OnSelectProvider(val provider : DataProviders) : OnBoardingEvents

    object OnPermissionGranted : OnBoardingEvents

    data object OnPermissionDenied : OnBoardingEvents

    object OnNextButtonClick : OnBoardingEvents

    data object OpenHealthConnectPermissionSettings : OnBoardingEvents
}