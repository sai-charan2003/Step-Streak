package com.charan.stepstreak.utils

import android.util.Log
import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import com.charan.stepstreak.data.model.StartOfWeekEnums
import com.charan.stepstreak.presentation.common.StepsData
import com.charan.stepstreak.presentation.common.PeriodStepsData
import com.charan.stepstreak.presentation.home.GraphData
import com.charan.stepstreak.presentation.widget.WidgetState


fun List<StepsRecordEntity>.toStepsData() : List<StepsData>{
    return this.map {
        it.toStepsData()
    }
}

fun StepsRecordEntity.toStepsData(): StepsData = StepsData(
    steps = steps ?: 0L,
    date = date ?: "",
    targetSteps = stepTarget ?: 0L,
    day = DateUtils.getWeekdayName(date ?: ""),
    formattedDate = DateUtils.formatDateForDisplay(date ?: ""),
    targetCompleted = (steps ?: 0L) >= (stepTarget ?: 0L),
    currentProgress = ((steps ?: 0L).toFloat() / (stepTarget ?: 1L).toFloat())
)

fun List<StepsRecordEntity>.toWidgetState(allData: List<StepsRecordEntity>,weekStartDate: StartOfWeekEnums): WidgetState {
    return WidgetState(
        currentWeekStepData = this.toWeekData(weekStartDate),
        streak = allData.getStreak(),
        motivationText = allData.getMotivationQuote(),
    )
}
fun List<StepsRecordEntity>.toWeekData(weekStartDate : StartOfWeekEnums): PeriodStepsData{
    val weekList = DateUtils.getWeekDayList(weekStartDate)
    val currentWeekData = this.toStepsData()
    val stepsData = weekList.map { day ->
        currentWeekData.find { it.day == day } ?: StepsRecordEntity(
            date = DateUtils.getDateFromWeekday(day, weekStartDate),
            steps = 0L,
        ).toStepsData()
    }
    return PeriodStepsData(
        averageSteps = currentWeekData.map { it.steps }.average().toLong(),
        stepsData = stepsData
    )
}

fun PeriodStepsData.toGraphData() : List<GraphData>{

    return this.stepsData.map {
        GraphData(
            steps = it.steps.toFloat(),
            day = it.day,
            date = DateUtils.getDayFromDate(it.date),
        )
    }
}

fun List<StepsRecordEntity>.toMonthData(currentMonth: String): PeriodStepsData {
    val firstDate = this.firstOrNull()?.date ?: DateUtils.getMonthStartDate(currentMonth)
    val monthName = DateUtils.getMonthName(firstDate)
    val allDatesInMonth = DateUtils.getAllDatesInMonth(firstDate)
    val stepsData = allDatesInMonth.map { date ->
        this.find { it.date == date }?.toStepsData() ?: StepsRecordEntity(
            date = date,
            steps = 0L,
        ).toStepsData()
    }
    return PeriodStepsData(
        averageSteps = stepsData.map { it.steps }.average().toLong(),
        stepsData = stepsData,
        totalSteps = stepsData.sumOf { it.steps },
        periodLabel = monthName
    )
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

fun Int.roundTo500() : Int {
    return if(this%500 ==0){
        this
    } else{
        ((this/500)+1)*500
    }
}

