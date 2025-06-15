package com.charan.stepstreak.presentation.common

data class StepsData(
    val steps : Long = 0L,
    val date : String = "",
    val targetSteps : Long = 10000,
    val day : String = "",
    val formattedDate : String = "",
    val targetCompleted : Boolean = false,
    val currentProgress : Float = 0f
)