package com.charan.stepstreak.presentation.onboarding

import androidx.activity.result.contract.ActivityResultContract

sealed interface OnBoardingViewEffect {

    data class ScrollPage(val page : Int) : OnBoardingViewEffect
    data object RequestPermission : OnBoardingViewEffect
    object OnBoardingComplete : OnBoardingViewEffect
    data object InstallHealthConnect : OnBoardingViewEffect
}