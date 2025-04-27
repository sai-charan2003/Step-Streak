package com.charan.stepstreak.presentation.onboarding

import com.charan.stepstreak.data.model.DataProviders

sealed interface OnBoardingEvents {

    object OnRequestPermission : OnBoardingEvents

    object OnChangePage : OnBoardingEvents

    data class OnSelectProvider(val provider : DataProviders) : OnBoardingEvents

    object OnPermissionGranted : OnBoardingEvents

    object OnBoardingComplete : OnBoardingEvents
}