package com.charan.stepstreak.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.charan.stepstreak.utils.DateUtils

@Entity(
    tableName = "steps_record",
    indices = [Index(value = ["uuid"], unique = true)])
data class StepsRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long?= null,
    val steps: Long? = null,
    val stepTarget : Long? = null,
    val uuid : String? = null,
    val date : String? = null
) {
    fun isTargetAchieved() : Boolean {
        val steps = this.steps ?: 0L
        val stepsTarget = this.stepTarget ?: 0L
        return steps >= stepsTarget
    }

    fun getPercentageOfStepsCompleted() : Float {
        val steps = this.steps ?: 0L
        val stepsTarget = this.stepTarget ?: 0L
        return steps.toFloat()/stepsTarget

    }

}
