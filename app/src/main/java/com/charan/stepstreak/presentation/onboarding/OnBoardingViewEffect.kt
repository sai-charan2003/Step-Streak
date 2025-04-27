package com.charan.stepstreak.presentation.onboarding

import androidx.activity.result.contract.ActivityResultContract

sealed interface OnBoardingViewEffect {

    object ScrollPage : OnBoardingViewEffect
    data object RequestPermission : OnBoardingViewEffect
    object OnBoardingComplete : OnBoardingViewEffect
}