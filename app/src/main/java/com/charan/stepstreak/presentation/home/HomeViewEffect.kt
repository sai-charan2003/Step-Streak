package com.charan.stepstreak.presentation.home

interface HomeViewEffect {
    data class ShowError(val message: String) : HomeViewEffect

}
