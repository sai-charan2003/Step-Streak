package com.charan.stepstreak.utils

import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import com.charan.stepstreak.presentation.home.StepsData
import com.charan.stepstreak.presentation.widget.WidgetState
import java.util.Date


fun List<StepsRecordEntity>.toStepsData() : List<StepsData>{
    return this.map {
        StepsData(
            steps = it.steps ?: 0L,
            date = it.date ?: "" ,
            targetSteps = it.stepTarget ?: 0L
        )
    }

}

fun List<StepsRecordEntity>.toWidgetState(): WidgetState{
    val stepsData : MutableList<com.charan.stepstreak.presentation.widget.StepsData> = mutableListOf()
    this.forEach {
        val steps = it.steps ?: 0L
        val targetSteps = it.stepTarget ?: 0L
        val stepData = com.charan.stepstreak.presentation.widget.StepsData(
            steps = it.steps ?: 0L,
            targetSteps = it.stepTarget ?: 0L,
            targetCompleted = steps >= targetSteps,
            date = DateUtils.getDateNumberFromIso(it.date ?: "").toString(),
            day = DateUtils.getWeekFromIso(it.date ?: "")

        )
        stepsData.add(stepData)
    }
    return WidgetState(
        stepsData = stepsData
    )
}


//fun List<StepsRecordEntity>.getStreak(): Int {
//    val streak = 0
//    val currentData = DateUtils.getCurrentDate()
//
//    this.forEach {
//        if()
//
//    }
//
//
//
//
//}
