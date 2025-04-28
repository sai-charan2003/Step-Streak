package com.charan.stepstreak.presentation.widget

data class WidgetState(
    val streak : Int = 0,
    val motiText : String ="",
    val stepsData: List<StepsData> =emptyList()

)

data class StepsData(
    val steps : Long = 0L,
    val targetSteps : Long = 0L,
    val targetCompleted : Boolean = false,
    val date : String = "",
    val day : String = ""

)
