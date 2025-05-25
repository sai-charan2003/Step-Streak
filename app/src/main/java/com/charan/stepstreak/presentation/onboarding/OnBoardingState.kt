package com.charan.stepstreak.presentation.onboarding

import com.charan.stepstreak.data.model.DataProviders

data class OnBoardingState(
    val isPermissionGranted : Boolean = false,
    val dataProviders: List<DataProviders> = emptyList(),
    val totalPages : Int = 2,
    val currentPage : Int = 0,
) {
    val buttonText: String
        get() = when {
            currentPage == 0 -> "Get Started"
            currentPage == 1 && isPermissionGranted -> "Finish"
            currentPage == 1 && !isPermissionGranted -> "Request Permission"
            else -> "Next"
        }
}

