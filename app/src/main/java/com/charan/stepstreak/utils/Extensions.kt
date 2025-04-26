package com.charan.stepstreak.utils

import com.charan.stepstreak.data.local.entity.StepsRecordEntity
import com.charan.stepstreak.presentation.home.StepsData


fun List<StepsRecordEntity>.toStepsData() : List<StepsData>{
    return this.map {
        StepsData(
            steps = it.steps ?: 0L,
            date = it.date ?: "" ,
            targetSteps = it.stepTarget ?: 0L
        )
    }

}