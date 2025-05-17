package com.charan.stepstreak.utils

import android.util.Log
import androidx.compose.ui.unit.Constraints
import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import com.charan.stepstreak.presentation.home.StepsData
import com.charan.stepstreak.presentation.widget.WidgetState
import com.charan.stepstreak.utils.Constants.walkingMotivationMessages
import com.himanshoe.charty.bar.model.BarData
import java.util.Date


fun List<StepsRecordEntity>.toStepsData() : List<StepsData>{
    return this.map {
        StepsData(
            steps = it.steps ?: 0L,
            date = it.date ?: "" ,
            targetSteps = it.stepTarget ?: 0L,
            day = DateUtils.getWeekFromIso(it.date ?: ""),
            formattedDate = DateUtils.formatDateWithSuffix(it.date ?: "")
        )
    }

}



fun List<StepsRecordEntity>.getTodaysStepsData() : StepsData?{
    val todayRecord = this.filter { DateUtils.convertLocalDateTimeToLocalDate(it.date.toString()) == DateUtils.getCurrentDate().toString() }
    return todayRecord.toStepsData().firstOrNull()

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

fun List<StepsData>.toBarChar() : List<BarData> {
    return this.map {
        BarData(
            yValue = it.steps.toFloat(),
            xValue = it.day,
        )
    }
}


fun List<StepsRecordEntity>.getStreak(): Int {
    if (this.isEmpty()) return 0


    val sortedRecords = this.sortedByDescending { it.date }

    var streak = 0
    var currentDate = DateUtils.getCurrentDate()

    val todayRecord = sortedRecords.find { it.date.toString() == currentDate.toString() }
    if (todayRecord != null && todayRecord.isTargetAchieved()) {
        streak = 1
        currentDate = currentDate.minusDays(1)
    } else {

        currentDate = currentDate.minusDays(1)
    }

    while (true) {
        val dateString = currentDate.toString()
        val recordForDate = sortedRecords.find { it.date.toString() == dateString }

        if (recordForDate != null && recordForDate.isTargetAchieved()) {
            streak++
        } else {
            break
        }

        currentDate = currentDate.minusDays(1)
    }

    return streak
}


fun List<StepsRecordEntity>.getMotivationQuote(): String {
    val todaySteps = this.find { it.date.toString() == DateUtils.getCurrentDate().toString() }
    val streak = this.getStreak()
    val stepsToday = todaySteps?.steps ?: 0L
    val percentCompleted = todaySteps?.getPercentageOfStepsCompleted() ?: 0f

    val quotes = mutableListOf<String>()

    if (streak == 0 && stepsToday > 0) {
        quotes.add(Constants.rebuildingStreakMessages.random())
    }

    if (streak == 0 && stepsToday == 0L) {
        quotes.add(Constants.brokenStreakMessages.random())
    }

    if (todaySteps?.isTargetAchieved() == true) {
        quotes.add(Constants.highProgressMessages.random())
    }

    if (percentCompleted in 0.5f..<1f) {
        quotes.add(Constants.midProgressMessages.random())
    }

    if (percentCompleted > 0f && percentCompleted < 0.5f) {
        quotes.add(Constants.lowProgressMessages.random())
    }

    if (streak > 2 && stepsToday == 0L) {
        quotes.add(Constants.ongoingStreakMessages.random())
    }

    return if (quotes.isNotEmpty()) quotes.random() else Constants.walkingMotivationMessages.random()
}

