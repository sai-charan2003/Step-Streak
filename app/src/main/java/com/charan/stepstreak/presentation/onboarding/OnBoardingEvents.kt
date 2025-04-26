package com.charan.stepstreak.presentation.onboarding

import com.charan.stepstreak.data.model.DataProviders

interface OnBoardingEvents {

    object OnRequestPermission : OnBoardingEvents

    object OnChangePage : OnBoardingEvents

    data class OnSelectProvider(val provider : DataProviders) : OnBoardingEvents
}